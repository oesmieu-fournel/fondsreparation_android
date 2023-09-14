package com.ecosystem.mobile.reparation.model.user

import com.ecosystem.mobile.reparation.service.api.scim_user.models.mobile_service.MobileServiceUser
import com.ecosystem.mobile.reparation.service.api.scim_user.models.scim_user.ScimUser

interface UserRepository {
    suspend fun getScimUser(userId: String) : ScimUser
    suspend fun getMobileServiceUser() : MobileServiceUser

}