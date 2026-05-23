package com.picker.app.domain.scanning

import com.picker.app.data.db.BatchDao
import com.picker.app.data.db.BatchEntity
import javax.inject.Inject

/**
 * Подбор партии по правилу FEFO (First Expire First Out): минимальный
 * batch.expires_at среди партий, удовлетворяющих фильтру
 * qty_on_hand >= qty_required. Если ни одна партия не покрывает, выбираем
 * с самой ранней датой годности, чтобы списать максимум остатка.
 */
class FefoPicker @Inject constructor(private val batchDao: BatchDao) {
    suspend fun pick(skuId: String, qtyRequired: Double): BatchEntity? {
        val candidates = batchDao.fefoCandidates(skuId, qtyRequired)
        return candidates.firstOrNull()
    }
}
