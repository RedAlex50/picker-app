package com.picker.app.domain.status

import com.picker.app.domain.model.OrderStatus
import javax.inject.Inject

/**
 * Ф6. Статусная машина наряда. Переходы только вперёд по графу
 * NEW → PICKING → PACKING → READY → HANDED_OVER. CANCELLED допустим с
 * любого активного состояния по инициативе клиента; откат возможен только
 * через ручную операцию диспетчера (не реализуется на ТСД).
 */
class OrderStatusMachine @Inject constructor() {

    private val transitions: Map<OrderStatus, Set<OrderStatus>> = mapOf(
        OrderStatus.NEW to setOf(OrderStatus.PICKING, OrderStatus.CANCELLED),
        OrderStatus.PICKING to setOf(OrderStatus.PACKING, OrderStatus.CANCELLED),
        OrderStatus.PACKING to setOf(OrderStatus.READY, OrderStatus.CANCELLED),
        OrderStatus.READY to setOf(OrderStatus.HANDED_OVER),
        OrderStatus.HANDED_OVER to emptySet(),
        OrderStatus.CANCELLED to emptySet(),
    )

    fun canTransition(from: OrderStatus, to: OrderStatus): Boolean =
        to in (transitions[from] ?: emptySet())

    fun require(from: OrderStatus, to: OrderStatus) {
        check(canTransition(from, to)) { "Forbidden transition: $from -> $to" }
    }
}
