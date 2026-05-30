package com.picker.app.domain.eventlog

import com.picker.app.data.db.EventOutboxDao
import com.picker.app.data.db.PickEventEntity
import com.picker.app.infrastructure.network.EventsApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.IOException
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ф10. Журнал операций — outbox-паттерн: событие сначала локально в SQLite,
 * затем батчи до 100 записей доставляются на сервер с экспоненциальным
 * backoff (предельный интервал 60 c). После 2xx — syncedAt проставляется
 * и записи можно архивировать.
 */
@Singleton
class EventLogger @Inject constructor(
    private val outbox: EventOutboxDao,
    private val api: EventsApi,
) {
    private val wakeups = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    suspend fun log(orderId: String, pickerId: String, type: String, payload: String) {
        outbox.enqueue(
            PickEventEntity(
                id = UUID.randomUUID().toString(),
                orderId = orderId,
                pickerId = pickerId,
                eventType = type,
                payload = payload,
                occurredAt = Instant.now(),
                syncedAt = null,
            )
        )
        wakeups.tryEmit(Unit)
    }

    suspend fun drainLoop() {
        var backoff = 1_000L
        while (true) {
            val batch = outbox.nextBatch(BATCH_SIZE)
            if (batch.isEmpty()) {
                wakeups.collect { return@collect }
                backoff = 1_000L
                continue
            }
            try {
                api.postBatch(batch.map(::toDto))
                outbox.markSynced(batch.map { it.id }, Instant.now())
                backoff = 1_000L
            } catch (_: IOException) {
                delay(backoff)
                backoff = (backoff * 2).coerceAtMost(MAX_BACKOFF_MS)
            }
        }
    }

    private fun toDto(e: PickEventEntity): EventsApi.EventDto = EventsApi.EventDto(
        id = e.id, orderId = e.orderId, pickerId = e.pickerId,
        eventType = e.eventType, payload = e.payload, occurredAt = e.occurredAt.toString(),
    )

    private companion object {
        const val BATCH_SIZE = 100
        const val MAX_BACKOFF_MS = 60_000L
    }
}
