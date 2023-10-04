package com.ecosystem.mobile.reparation.model.user.use_case

import android.util.Log
import com.ecosystem.mobile.reparation.model.user.UserRepository
import com.ecosystem.mobile.reparation.service.api.common.ApiResult
import com.ecosystem.mobile.reparation.service.api.mobile_service.models.MobileServiceUser
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException

class GetMobileServiceUserData(private val repository: UserRepository) {
    suspend operator fun invoke(): ApiResult<MobileServiceUser> {
        return try {
            val user = repository.getMobileServiceUser()
            Log.d("GetMobileServiceUserData", "call success")
            ApiResult.Success(user)
        } catch (e: HttpException) {
            Log.d("GetMobileServiceUserData", "call error")
            ApiResult.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Log.d("GetMobileServiceUserData", "call error")
            ApiResult.Error("Couldn't reach server. Check your connexion")
        }
    }
}