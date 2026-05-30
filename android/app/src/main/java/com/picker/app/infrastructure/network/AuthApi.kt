package com.picker.app.infrastructure.network

import com.picker.app.infrastructure.crypto.JwtToken
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    data class ExchangeRequest(val pickerId: String)
    @POST("/api/v1/auth/jwt")
    suspend fun exchangeForJwt(pickerId: String): JwtToken =
        exchange(ExchangeRequest(pickerId))

    @POST("/api/v1/auth/jwt")
    suspend fun exchange(@Body req: ExchangeRequest): JwtToken
}
