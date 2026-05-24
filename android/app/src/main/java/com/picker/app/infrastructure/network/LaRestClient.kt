package com.picker.app.infrastructure.network

import retrofit2.http.Body
import retrofit2.http.POST

interface LaRestClient {
    data class SubstitutionRequest(val orderId: String, val itemId: String, val originalSkuId: String)
    data class SubstitutionDecision(val approved: Boolean, val suggestedSkuId: String?)

    @POST("/api/v1/la/substitutions")
    suspend fun askClient(@Body req: SubstitutionRequest): SubstitutionDecision
}
