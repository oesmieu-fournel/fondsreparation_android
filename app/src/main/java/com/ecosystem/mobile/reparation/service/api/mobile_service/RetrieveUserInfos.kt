package com.ecosystem.mobile.reparation.service.api.mobile_service

import android.content.Context
import com.ecosystem.mobile.reparation.data.SharedPreferenceRepository
import com.ecosystem.mobile.reparation.model.user.use_case.GetMobileServiceUserData
import com.ecosystem.mobile.reparation.repository.UserRepositoryImpl
import com.ecosystem.mobile.reparation.service.api.common.ApiResult
import com.ecosystem.mobile.reparation.service.api.mobile_service.models.MobileServiceUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class RetrieveUserInfos(private val context: Context) {

    private val getMobileServiceUserData: GetMobileServiceUserData by lazy {
        return@lazy GetMobileServiceUserData(UserRepositoryImpl())
    }

    fun userName(): String =
        runBlocking {
            var userName = ""
            try {
                userName = SharedPreferenceRepository(context).userPreferencesFlow.first().username
            } finally {

            }
            if (userName.isEmpty()) {
                val mobileServiceUserResult = getMobileServiceUserData()
                if (mobileServiceUserResult is ApiResult.Success<MobileServiceUser>) {
                    mobileServiceUserResult.data?.detail?.login_name?.firstOrNull()
                        ?.let { login_name ->
                            userName = login_name
                            SharedPreferenceRepository(context).updateUsername(userName)
                        }
                }
            }
            return@runBlocking userName
        }

}
