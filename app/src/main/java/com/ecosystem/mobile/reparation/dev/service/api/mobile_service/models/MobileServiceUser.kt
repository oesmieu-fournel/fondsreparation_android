package com.ecosystem.mobile.reparation.dev.service.api.mobile_service.models

data class MobileServiceUser(
    val detail: Detail,
    val emails: List<Email>,
    val id: String,
    val name: Name,
    val roles: List<String>,
    val schemas: List<String>,
    val userName: String
)