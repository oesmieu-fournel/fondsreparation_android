package com.ecosystem.mobile.reparation.service.api.scim_user.models.scim_user

data class ScimUser(
    val active: Boolean,
    val id: String,
    val userName: String,
    val userType: String
)