package com.ecosystem.mobile.reparation.service.api.mobile_service

import com.ecosystem.mobile.reparation.service.api.mobile_service.models.MobileServiceUser
import retrofit2.http.GET

interface UserApi {
    @GET("Me")
    suspend fun getMobileServiceUserData() : MobileServiceUser

}