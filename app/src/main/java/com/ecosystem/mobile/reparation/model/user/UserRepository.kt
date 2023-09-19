package com.ecosystem.mobile.reparation.model.user

import com.ecosystem.mobile.reparation.service.api.mobile_service.models.MobileServiceUser

interface UserRepository {
    suspend fun getMobileServiceUser() : MobileServiceUser

}