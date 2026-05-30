package com.picker.app.infrastructure.network

import com.picker.app.domain.model.PickOrder
import retrofit2.http.GET
import retrofit2.http.Query

interface PosRestClient {
    @GET("/api/v1/orders")
    suspend fun fetchQueue(@Query("shift_id") shiftId: String): List<PickOrder>
}
