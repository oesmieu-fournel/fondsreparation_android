package com.ecosystem.mobile.reparation.dev.service.api.aws_file_upload


import com.sap.cloud.mobile.flows.compose.core.FlowContextRegistry
import com.sap.cloud.mobile.foundation.common.ClientProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object AWSFilesClient {

    private val endPoint : String = FlowContextRegistry.flowContext.appConfig?.serviceUrl ?: ""

    /* Destinations */
    private val mobileServiveDestination : String = "com.ecosystem.mobile.reparation.files/"

    val apiFondsReparation: ApiFondsReparation by lazy {

        return@lazy Retrofit.Builder()
            .baseUrl(endPoint + mobileServiveDestination)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiFondsReparation::class.java)
    }
    fun awsFileUploadClient(certifiedUploadBaseUrl : String): AWSUploadFileApi{
        return Retrofit.Builder()
            .baseUrl(certifiedUploadBaseUrl)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AWSUploadFileApi::class.java)
    }

    private fun getClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return ClientProvider.get()
            .newBuilder()
            .addInterceptor(Interceptor { chain ->
                val r = chain.request()
                    .newBuilder()
                    //.addHeader("Accept", "application/json")
                    .build()
                chain.proceed(r)
            })
            .addInterceptor(interceptor)
            .build()
    }
}