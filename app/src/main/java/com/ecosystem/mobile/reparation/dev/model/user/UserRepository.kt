package com.ecosystem.mobile.reparation.dev.model.user

import com.ecosystem.mobile.reparation.dev.service.api.mobile_service.models.MobileServiceUser


interface UserRepository {
    suspend fun getMobileServiceUser() : MobileServiceUser

}