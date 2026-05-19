package com.picker.app.infrastructure.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Подписка на push-обновления очереди заказов по WebSocket.
 * Reconnect с экспоненциальным backoff обрабатывает вышестоящий уровень.
 */
@Singleton
class OrderPushClient @Inject constructor(
    private val http: OkHttpClient,
    moshi: Moshi,
) {
    sealed interface Event {
        data class OrderUpserted(val orderId: String, val status: String) : Event
        data class OrderCancelled(val orderId: String) : Event
    }

    private val adapter: JsonAdapter<Map<String, Any>> =
        moshi.adapter(Map::class.java) as JsonAdapter<Map<String, Any>>

    fun subscribe(shiftId: String, token: String): Flow<Event> = callbackFlow {
        val req = Request.Builder()
            .url("wss://pos.internal/api/v1/orders/stream?shift_id=$shiftId")
            .addHeader("Authorization", "Bearer $token")
            .build()

        val ws: WebSocket = http.newWebSocket(req, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val payload = adapter.fromJson(text) ?: return
                val type = payload["type"] as? String ?: return
                val orderId = payload["order_id"] as? String ?: return
                when (type) {
                    "ORDER_UPSERTED" -> trySend(Event.OrderUpserted(orderId, payload["status"] as String))
                    "ORDER_CANCELLED" -> trySend(Event.OrderCancelled(orderId))
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, r: Response?) { close(t) }
        })

        awaitClose { ws.close(1000, "client closed") }
    }
}
