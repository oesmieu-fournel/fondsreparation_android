package com.ecosystem.mobile.reparation.service.api.mobile_service

import android.content.Context
import android.util.Log
import com.ecosystem.mobile.reparation.data.SharedPreferenceRepository
import com.ecosystem.mobile.reparation.model.user.use_case.GetMobileServiceUserData
import com.ecosystem.mobile.reparation.repository.UserRepositoryImpl
import com.ecosystem.mobile.reparation.service.api.common.ApiResult
import com.ecosystem.mobile.reparation.service.api.mobile_service.models.MobileServiceUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RetrieveUserInfos(private val context: Context) {

    private val getMobileServiceUserData: GetMobileServiceUserData by lazy {
        return@lazy GetMobileServiceUserData(UserRepositoryImpl())
    }

    suspend fun _userName(): String = coroutineScope {
        val userPref = withContext(Dispatchers.Default) {
            SharedPreferenceRepository(context).userPreferencesFlow.first()
        }
        if (userPref.username.isNotEmpty()) {
            Log.d("RetrieveUserInfos", "retrieving username from preferences ${userPref.username}")
            userPref.username
        } else {
            var username = ""
            Log.d("RetrieveUserInfos", "retrieving username from API Call ${userPref.username}")
            val mobileServiceUserResult =
                withContext(Dispatchers.IO) { getMobileServiceUserData() }
            if (mobileServiceUserResult is ApiResult.Success<MobileServiceUser>) {
                Log.i("log", mobileServiceUserResult.data.toString())
                mobileServiceUserResult.data?.detail?.login_name?.firstOrNull()
                    ?.let { login_name ->
                        SharedPreferenceRepository(context).updateUsername(
                            login_name
                        )
                        username = login_name
                    }
            }
            username
        }
    }

    fun userName() = runBlocking {
        _userName()
    }
}
