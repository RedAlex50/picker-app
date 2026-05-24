package com.picker.app.domain.substitution

import com.picker.app.data.db.PickItemDao
import com.picker.app.data.db.PickItemEntity
import com.picker.app.data.db.SubstitutionEntity
import com.picker.app.domain.model.ItemStatus
import com.picker.app.infrastructure.network.LaRestClient
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

/**
 * Ф5. Запрос аналогов в LA и согласование замены с клиентом.
 * Таймаут ожидания — 180 c; при таймауте или отказе — pick_item.status := SHORT.
 * При одобрении создаётся запись substitution и обновляется pick_item.sku_id.
 */
class SubstitutionUseCase @Inject constructor(
    private val la: LaRestClient,
    private val items: PickItemDao,
) {
    sealed interface Outcome {
        data class Substituted(val newSkuId: String) : Outcome
        data object Short : Outcome
    }

    suspend fun request(item: PickItemEntity): Outcome {
        val decision = try {
            withTimeout(180.seconds.toJavaDuration().toMillis()) {
                la.askClient(LaRestClient.SubstitutionRequest(item.orderId, item.id, item.skuId))
            }
        } catch (_: TimeoutCancellationException) {
            null
        }

        return if (decision?.approved == true && decision.suggestedSkuId != null) {
            items.update(item.copy(skuId = decision.suggestedSkuId, status = ItemStatus.SUBSTITUTED.name))
            Outcome.Substituted(decision.suggestedSkuId)
        } else {
            items.update(item.copy(status = ItemStatus.SHORT.name))
            Outcome.Short
        }
    }
}
