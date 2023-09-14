package com.ecosystem.mobile.reparation.service.api.scim_user.models.mobile_service

data class Detail(
    val app_tid: List<String>,
    val aud: List<String>,
    val email: List<String>,
    val family_name: List<String>,
    val given_name: List<String>,
    val groups: List<String>,
    val nonce: List<String>,
    val remoteEntityId: List<String>,
    val scim_id: List<String>,
    val sid: List<String>,
    val sub: List<String>,
    val user_uuid: List<String>,
    val zone_uuid: List<String>
)