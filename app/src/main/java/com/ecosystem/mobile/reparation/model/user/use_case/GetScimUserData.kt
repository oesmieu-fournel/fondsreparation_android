package com.ecosystem.mobile.reparation.model.user.use_case

import com.ecosystem.mobile.reparation.model.user.UserRepository
import com.ecosystem.mobile.reparation.service.api.scim_user.models.common.ApiResult
import com.ecosystem.mobile.reparation.service.api.scim_user.models.scim_user.ScimUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

class GetScimUserData(private val repository: UserRepository) {
    private val ioDispatcher = Dispatchers.IO

    /*operator fun invoke(userId: String): Flow<ApiResult<ScimUser>> = flow {
        try {
            val user = repository.getScimUser(userId)
            emit(ApiResult.Success(user))
        } catch (e: HttpException) {
            emit(ApiResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(ApiResult.Error("Couldn't reach server. Check your connexion"))
        }
    }.flowOn(ioDispatcher)*/
    suspend operator fun invoke(userId: String): ApiResult<ScimUser> {
        return try {
            val user = repository.getScimUser(userId)
            ApiResult.Success(user)
        } catch (e: HttpException) {
            ApiResult.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            ApiResult.Error("Couldn't reach server. Check your connexion")
        }
    }
}