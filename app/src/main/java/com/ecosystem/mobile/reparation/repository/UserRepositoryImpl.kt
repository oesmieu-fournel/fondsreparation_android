package com.ecosystem.mobile.reparation.repository

import com.ecosystem.mobile.reparation.model.user.UserRepository
import com.ecosystem.mobile.reparation.service.api.mobile_service.UserClient
import com.ecosystem.mobile.reparation.service.api.mobile_service.models.MobileServiceUser

class UserRepositoryImpl : UserRepository {
    private val client = UserClient()
    private val mobileServiceUserApi = client.msUserInfosService

    override suspend fun getMobileServiceUser(): MobileServiceUser {
        return mobileServiceUserApi.getMobileServiceUserData()
    }
}