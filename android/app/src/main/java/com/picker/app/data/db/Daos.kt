package com.picker.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface PickerDao {
    @Query("SELECT * FROM picker WHERE login = :login LIMIT 1")
    suspend fun findByLogin(login: String): PickerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(picker: PickerEntity)
}

@Dao
interface SkuDao {
    @Query("SELECT * FROM sku WHERE ean13 = :ean LIMIT 1")
    suspend fun findByEan13(ean: String): SkuEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<SkuEntity>)
}

@Dao
interface CellDao {
    @Query("SELECT * FROM cell WHERE code = :code LIMIT 1")
    suspend fun findByCode(code: String): CellEntity?
}

@Dao
interface BatchDao {
    // FEFO: earliest expiry first, only batches with enough stock
    @Query("""
        SELECT * FROM batch
        WHERE skuId = :skuId AND qtyOnHand >= :need
        ORDER BY expiresAt ASC, id ASC
    """)
    suspend fun fefoCandidates(skuId: String, need: Double): List<BatchEntity>

    @Query("UPDATE batch SET qtyOnHand = qtyOnHand - :qty WHERE id = :batchId")
    suspend fun decrementStock(batchId: String, qty: Double)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM pick_order WHERE shiftId = :shiftId ORDER BY priority DESC, slotAt ASC")
    fun observeQueue(shiftId: String): Flow<List<PickOrderEntity>>

    @Query("SELECT * FROM pick_order WHERE id = :id")
    suspend fun byId(id: String): PickOrderEntity?

    @Query("UPDATE pick_order SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(orders: List<PickOrderEntity>)
}

@Dao
interface PickItemDao {
    @Query("SELECT * FROM pick_item WHERE orderId = :orderId")
    fun observeByOrder(orderId: String): Flow<List<PickItemEntity>>

    @Query("SELECT COUNT(*) FROM pick_item WHERE orderId = :orderId AND status = 'PENDING'")
    suspend fun pendingCount(orderId: String): Int

    @Update suspend fun update(item: PickItemEntity)
}

@Dao
interface BoxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(box: BoxEntity)

    @Query("SELECT * FROM box WHERE orderId = :orderId")
    suspend fun byOrder(orderId: String): List<BoxEntity>
}

@Dao
interface EventOutboxDao {
    @Insert suspend fun enqueue(event: PickEventEntity)

    @Query("SELECT * FROM pick_event WHERE syncedAt IS NULL ORDER BY occurredAt ASC LIMIT :limit")
    suspend fun nextBatch(limit: Int): List<PickEventEntity>

    @Query("UPDATE pick_event SET syncedAt = :t WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>, t: Instant)
}
