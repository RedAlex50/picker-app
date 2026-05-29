package com.picker.app.domain.handover

import com.picker.app.data.db.BoxDao
import com.picker.app.data.repository.OrderRepository
import com.picker.app.domain.model.OrderStatus
import com.picker.app.domain.status.OrderStatusMachine
import javax.inject.Inject

/**
 * Ф9. Передача заказа курьеру. Скан QR курьерского бейджа и последовательно
 * QR каждого бокса. При совпадении ВСЕХ боксов выполняется атомарный переход
 * pick_order.status := HANDED_OVER и регистрация события HANDOVER.
 */
class HandoverUseCase @Inject constructor(
    private val orders: OrderRepository,
    private val boxDao: BoxDao,
    private val machine: OrderStatusMachine,
) {
    sealed interface Result {
        data object Success : Result
        data class Mismatch(val missing: List<String>) : Result
        data object UnknownCourier : Result
    }

    suspend fun handover(orderId: String, courierBadgeQr: String, scannedBoxIds: List<String>): Result {
        if (!courierBadgeQr.startsWith("COURIER:")) return Result.UnknownCourier
        val expected = boxDao.byOrder(orderId).map { it.id }.toSet()
        val scanned = scannedBoxIds.toSet()
        if (scanned != expected) return Result.Mismatch((expected - scanned).toList())
        machine.require(OrderStatus.READY, OrderStatus.HANDED_OVER)
        orders.updateStatus(orderId, OrderStatus.HANDED_OVER)
        return Result.Success
    }
}
