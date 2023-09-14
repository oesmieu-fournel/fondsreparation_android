package com.ecosystem.mobile.reparation.repository

import com.ecosystem.mobile.reparation.model.user.UserRepository
import com.ecosystem.mobile.reparation.service.api.scim_user.UserClient
import com.ecosystem.mobile.reparation.service.api.scim_user.models.mobile_service.MobileServiceUser
import com.ecosystem.mobile.reparation.service.api.scim_user.models.scim_user.ScimUser

class UserRepositoryImpl : UserRepository {
    private val client = UserClient()
    private val scimUserApi = client.scimUserInfosService
    private val mobileServiceUserApi = client.msUserInfosService
    override suspend fun getScimUser(userEmail: String): ScimUser {
        return scimUserApi.getScimUserData(userEmail)
    }

    override suspend fun getMobileServiceUser(): MobileServiceUser {
        return mobileServiceUserApi.getMobileServiceUserData()
    }
}