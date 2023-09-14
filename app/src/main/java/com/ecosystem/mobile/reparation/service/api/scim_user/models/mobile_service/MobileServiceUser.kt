package com.ecosystem.mobile.reparation.service.api.scim_user.models.mobile_service

data class MobileServiceUser(
    val detail: Detail,
    val emails: List<Email>,
    val id: String,
    val name: Name,
    val roles: List<String>,
    val schemas: List<String>,
    val userName: String
)