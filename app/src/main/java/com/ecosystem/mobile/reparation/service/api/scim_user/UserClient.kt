package com.ecosystem.mobile.reparation.service.api.scim_user

import com.sap.cloud.mobile.flows.compose.core.FlowContextRegistry
import com.sap.cloud.mobile.foundation.common.ClientProvider
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UserClient {

    private val endPoint : String = FlowContextRegistry.flowContext.appConfig?.serviceUrl ?: ""

    /* Destinations */
    private val mobileServiveDestination : String = "mobileservices/application/com.ecosystem.mobile.reparation/roleservice/application/com.ecosystem.mobile.reparation/v2/"
    private val idpDestination : String = "com.ecosystem.mobile.idp/scim/"

    val msUserInfosService: UserApi by lazy {

        return@lazy Retrofit.Builder()
            .baseUrl(endPoint + mobileServiveDestination)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }


    val scimUserInfosService: UserApi by lazy {

        return@lazy Retrofit.Builder()
            .baseUrl(endPoint + idpDestination)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
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