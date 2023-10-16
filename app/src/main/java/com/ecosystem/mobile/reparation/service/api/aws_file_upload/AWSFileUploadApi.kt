package com.ecosystem.mobile.reparation.service.api.aws_file_upload

import com.ecosystem.mobile.reparation.service.api.aws_file_upload.models.AWSFileInformations
import com.ecosystem.mobile.reparation.service.api.aws_file_upload.models.AWSUploadLink
import com.ecosystem.mobile.reparation.service.api.mobile_service.models.MobileServiceUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface AWSFileUploadApi {

        @GET("{serviceOrder}")
        suspend fun generateUploadLink(@Path(value = "serviceOrder") serviceOrder : String, @Body fileInformation : AWSFileInformations) : AWSUploadLink

}