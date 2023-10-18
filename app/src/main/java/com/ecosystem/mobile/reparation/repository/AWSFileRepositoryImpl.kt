package com.ecosystem.mobile.reparation.repository

import com.ecosystem.mobile.reparation.model.files.FileInformation
import com.ecosystem.mobile.reparation.model.files.FilesInformations
import com.ecosystem.mobile.reparation.model.files.FilesRepository
import com.ecosystem.mobile.reparation.service.api.aws_file_upload.AWSFilesClient
import com.ecosystem.mobile.reparation.service.api.aws_file_upload.AWSUploadFileApi
import com.ecosystem.mobile.reparation.service.api.aws_file_upload.models.AWSCertifiedUploadLink
import com.ecosystem.mobile.reparation.service.api.aws_file_upload.models.AWSFileInformations
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.MalformedURLException
import java.net.URL

class AWSFileRepositoryImpl : FilesRepository {
    private val awsFilesService = AWSFilesClient.awsCertifiedFilesService

    override suspend fun uploadFiles(
        serviceOrder: String,
        filesInformations: FilesInformations
    ): FilesInformations {
        var filesInformationUpdated = mutableListOf<FileInformation>()

        filesInformations.data.forEach { fileInformation ->

            val certifiedLinkResponse = awsFilesService.generateCertifiedUploadLink(
                serviceOrder,
                AWSFileInformations(
                    FileName = fileInformation.fileName,
                    FileType = fileInformation.fileType
                )
            )
            if (certifiedLinkResponse.isSuccessful) {
                certifiedLinkResponse.body()?.let { certifiedLinkPayload ->
                    uploadFile(certifiedLinkPayload, fileInformation)?.let {
                        filesInformationUpdated.add(it)
                    }
                }
            }


        }

        return FilesInformations(filesInformationUpdated)
    }

    private suspend fun uploadFile(
        certifiedLinkPayload: AWSCertifiedUploadLink,
        fileInformation: FileInformation,
    ): FileInformation? {
        fileInformation.bytes
        val content = fileInformation.bytes

        val awsClient = Retrofit.Builder()
            .baseUrl("http://www.dummy.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AWSUploadFileApi::class.java)


        val uploadFileResponse = awsClient.uploadFile(
            url = certifiedLinkPayload.url, file =
            content.toRequestBody(
                "application/octet".toMediaTypeOrNull(),
                0,
                content.size
            )
        )

        return if (uploadFileResponse.isSuccessful) {
            try {
                val url = URL(certifiedLinkPayload.url)
                val baseUrl = url.protocol + "://"+ url.host + "/"
                val filepath = baseUrl + certifiedLinkPayload.fileLocation
                val fileName = filepath.substring(filepath.lastIndexOf("/") + 1)
                fileInformation.copy(
                    url = filepath,
                    fileName = fileName,
                    bytes = ByteArray(0),
                )
            }catch (exc : MalformedURLException){
                null
            }

        } else
            null
    }
}