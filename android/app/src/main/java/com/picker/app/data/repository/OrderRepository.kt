package com.picker.app.data.repository

import com.picker.app.data.db.OrderDao
import com.picker.app.data.db.PickItemDao
import com.picker.app.data.db.PickOrderEntity
import com.picker.app.domain.model.OrderStatus
import com.picker.app.domain.model.PickOrder
import com.picker.app.infrastructure.network.PosRestClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderDao: OrderDao,
    private val itemDao: PickItemDao,
    private val pos: PosRestClient,
) {
    /** Очередь нарядов смены: priority DESC, slot_at ASC. */
    fun observeQueue(shiftId: String): Flow<List<PickOrder>> =
        orderDao.observeQueue(shiftId).map { rows -> rows.map(::toDomain) }

    suspend fun pullFromPos(shiftId: String) {
        val remote = pos.fetchQueue(shiftId)
        orderDao.upsertAll(remote.map(::toEntity))
    }

    suspend fun updateStatus(id: String, status: OrderStatus) =
        orderDao.updateStatus(id, status.name)

    suspend fun pendingItems(orderId: String): Int = itemDao.pendingCount(orderId)

    private fun toDomain(e: PickOrderEntity) = PickOrder(
        id = e.id,
        orderNo = e.orderNo,
        status = OrderStatus.valueOf(e.status),
        priority = e.priority.toInt(),
        slotAt = e.slotAt,
        items = emptyList(),
    )

    private fun toEntity(o: PickOrder) = PickOrderEntity(
        id = o.id,
        shiftId = "",
        orderNo = o.orderNo,
        status = o.status.name,
        priority = o.priority.toShort(),
        slotAt = o.slotAt,
        createdAt = o.slotAt,
    )
}
