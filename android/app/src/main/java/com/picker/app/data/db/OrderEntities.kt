package com.picker.app.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "shift")
data class ShiftEntity(
    @PrimaryKey val id: String,
    val pickerId: String,
    val darkstoreId: String,
    val startedAt: Instant,
    val endedAt: Instant?,
)

@Entity(
    tableName = "batch",
    foreignKeys = [
        ForeignKey(SkuEntity::class, ["id"], ["skuId"]),
        ForeignKey(CellEntity::class, ["id"], ["cellId"]),
    ],
    indices = [Index("skuId"), Index("cellId"), Index("expiresAt")],
)
data class BatchEntity(
    @PrimaryKey val id: String,
    val skuId: String,
    val cellId: String,
    val expiresAt: LocalDate,
    val qtyOnHand: Double,
)

@Entity(tableName = "pick_order", indices = [Index(value = ["orderNo"], unique = true), Index("status")])
data class PickOrderEntity(
    @PrimaryKey val id: String,
    val shiftId: String,
    val orderNo: String,
    val status: String,
    val priority: Short,
    val slotAt: Instant,
    val createdAt: Instant,
)

@Entity(tableName = "pick_item", indices = [Index("orderId"), Index("status")])
data class PickItemEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val skuId: String,
    val cellId: String?,
    val batchId: String?,
    val qtyRequired: Double,
    val qtyPicked: Double,
    val status: String,
)

@Entity(tableName = "box", indices = [Index("orderId")])
data class BoxEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val tempZone: String,
    val labelZpl: String?,
    val sealedAt: Instant?,
)

@Entity(tableName = "substitution", indices = [Index("itemId")])
data class SubstitutionEntity(
    @PrimaryKey val id: String,
    val itemId: String,
    val originalSkuId: String,
    val suggestedSkuId: String,
    val decision: String,
    val decidedAt: Instant?,
)

@Entity(tableName = "pick_event", indices = [Index("orderId"), Index("occurredAt")])
data class PickEventEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val pickerId: String,
    val eventType: String,
    val payload: String,
    val occurredAt: Instant,
    val syncedAt: Instant?,
)
