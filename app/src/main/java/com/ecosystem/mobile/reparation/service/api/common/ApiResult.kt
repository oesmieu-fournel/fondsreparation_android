package com.ecosystem.mobile.reparation.service.api.common
sealed class ApiResult<T>(val data : T? = null, val message : String? = null ) {
    class Success<T>(data : T? = null) : ApiResult<T>(data,)
    class Error<T>(message : String) : ApiResult<T>(null, message)
}