package com.ecosystem.mobile.reparation.service.mobile_service

import android.content.Context
import android.content.SharedPreferences
import com.ecosystem.mobile.reparation.data.SharedPreferenceRepository
import com.ecosystem.mobile.reparation.model.user.use_case.GetMobileServiceUserData
import com.ecosystem.mobile.reparation.model.user.use_case.GetScimUserData
import com.ecosystem.mobile.reparation.repository.UserRepositoryImpl
import com.ecosystem.mobile.reparation.service.api.scim_user.models.common.ApiResult
import com.ecosystem.mobile.reparation.service.api.scim_user.models.mobile_service.MobileServiceUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RetrieveUser(private val context: Context) {


    private val getScimUserData: GetScimUserData by lazy {
        return@lazy GetScimUserData(UserRepositoryImpl())
    }


    private val getMobileServiceUserData: GetMobileServiceUserData by lazy {
        return@lazy GetMobileServiceUserData(UserRepositoryImpl())
    }

    suspend fun userName(): String =
        withContext(Dispatchers.Default) {
            var userName = ""
            val mobileServiceUserResult = async { getMobileServiceUserData() }.await()
            if (mobileServiceUserResult is ApiResult.Success<MobileServiceUser>) {
                mobileServiceUserResult.data?.detail?.scim_id?.firstOrNull()?.let { scimId ->
                    val scimUserResult = async { getScimUserData(scimId) }.await()
                    userName = scimUserResult.data?.userName ?: ""
                }
            }
            SharedPreferenceRepository(context).updateUsername(userName)
            return@withContext userName
        }

}

interface ServiceListener<T> {
    fun onResponse(response: ApiResult<T>)
}