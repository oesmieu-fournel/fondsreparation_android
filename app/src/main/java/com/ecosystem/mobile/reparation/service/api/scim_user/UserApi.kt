package com.ecosystem.mobile.reparation.service.api.scim_user

import com.ecosystem.mobile.reparation.service.api.scim_user.models.mobile_service.MobileServiceUser
import com.ecosystem.mobile.reparation.service.api.scim_user.models.scim_user.ScimUser
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("Users/{user_id}")
    suspend fun getScimUserData(@Path(value = "user_id") userId : String) : ScimUser

    @GET("Me")
    suspend fun getMobileServiceUserData() : MobileServiceUser

}