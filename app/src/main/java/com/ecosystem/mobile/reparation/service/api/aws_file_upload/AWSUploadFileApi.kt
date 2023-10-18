package com.ecosystem.mobile.reparation.service.api.aws_file_upload

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url


interface AWSUploadFileApi {
       /* @PUT("{certifiedUploadRequest}")
        suspend fun uploadFile(@Path(value = "certifiedUploadRequest", encoded = true) certifiedUploadRequest : String , @Body body : RequestBody) : Response<Any>*/

    @PUT
    suspend fun uploadFile(@Url url: String?, @Body file: RequestBody?): Response<ResponseBody>
    }