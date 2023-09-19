package com.ecosystem.mobile.reparation.model.user.use_case

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
            ApiResult.Success(user)
        } catch (e: HttpException) {
            ApiResult.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            ApiResult.Error("Couldn't reach server. Check your connexion")
        }
    }
}