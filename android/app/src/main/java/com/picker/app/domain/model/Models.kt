package com.picker.app.domain.model

import java.time.Instant
import java.time.LocalDate

data class Picker(val id: String, val login: String, val fullName: String)
data class Shift(val id: String, val pickerId: String, val startedAt: Instant)

enum class TempZone { AMBIENT, CHILLED, FROZEN }
enum class OrderStatus { NEW, PICKING, PACKING, READY, HANDED_OVER, CANCELLED }
enum class ItemStatus { PENDING, PICKED, SUBSTITUTED, SHORT }

data class Sku(
    val id: String,
    val ean13: String,
    val name: String,
    val tempZone: TempZone,
    val grossWeightG: Int,
)

data class Cell(val id: String, val code: String, val zone: String, val rowNo: Int, val level: Int)

data class Batch(
    val id: String,
    val skuId: String,
    val cellId: String,
    val expiresAt: LocalDate,
    val qtyOnHand: Double,
)

data class PickOrder(
    val id: String,
    val orderNo: String,
    val status: OrderStatus,
    val priority: Int,
    val slotAt: Instant,
    val items: List<PickItem>,
)

data class PickItem(
    val id: String,
    val orderId: String,
    val skuId: String,
    val cellId: String?,
    val batchId: String?,
    val qtyRequired: Double,
    val qtyPicked: Double,
    val status: ItemStatus,
)
