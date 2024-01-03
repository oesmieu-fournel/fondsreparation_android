package com.ecosystem.mobile.reparation.dev.repository

import com.ecosystem.mobile.reparation.dev.model.user.UserRepository
import com.ecosystem.mobile.reparation.dev.service.api.mobile_service.UserClient
import com.ecosystem.mobile.reparation.dev.service.api.mobile_service.models.MobileServiceUser


class UserRepositoryImpl : UserRepository {
    private val client = UserClient()
    private val mobileServiceUserApi = client.msUserInfosService

    override suspend fun getMobileServiceUser(): MobileServiceUser {
        return mobileServiceUserApi.getMobileServiceUserData()
    }
}