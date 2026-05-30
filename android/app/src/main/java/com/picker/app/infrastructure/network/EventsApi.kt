package com.picker.app.infrastructure.network

import retrofit2.http.Body
import retrofit2.http.POST

interface EventsApi {
    data class EventDto(
        val id: String,
        val orderId: String,
        val pickerId: String,
        val eventType: String,
        val payload: String,
        val occurredAt: String,
    )

    @POST("/api/v1/events/batch")
    suspend fun postBatch(@Body events: List<EventDto>)
}
