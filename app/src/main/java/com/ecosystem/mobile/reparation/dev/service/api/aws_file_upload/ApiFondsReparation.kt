package com.ecosystem.mobile.reparation.dev.service.api.aws_file_upload

import com.ecosystem.mobile.reparation.dev.service.api.aws_file_upload.models.AWSFileInformations
import com.ecosystem.mobile.reparation.dev.service.api.aws_file_upload.models.AWSCertifiedUploadLink
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiFondsReparation {

        @POST("admin/upload/serviceOrder/{serviceOrder}")
        suspend fun generateCertifiedUploadLink(@Path(value = "serviceOrder") serviceOrder : String, @Body fileInformation : AWSFileInformations) : Response<AWSCertifiedUploadLink>

        @POST("admin/claim_to_analyze")
        suspend fun sendToAnalyze( @Body bodyPayload : HashMap<String, Any>) : Response<Object>

}