package com.picker.app.domain.usecase

import com.picker.app.data.repository.OrderRepository
import com.picker.app.domain.model.PickOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Ф2. Получение очереди нарядов на сборку. Источник истины — POS, локальный
 * кэш в Room. Сортировка priority DESC, slot_at ASC, экранная страница до 50.
 */
class OrderQueueUseCase @Inject constructor(private val orders: OrderRepository) {
    suspend fun refresh(shiftId: String) = orders.pullFromPos(shiftId)
    fun observe(shiftId: String): Flow<List<PickOrder>> = orders.observeQueue(shiftId)
}
