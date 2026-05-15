package com.picker.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        PickerEntity::class, ShiftEntity::class, SkuEntity::class, CellEntity::class,
        BatchEntity::class, PickOrderEntity::class, PickItemEntity::class,
        BoxEntity::class, SubstitutionEntity::class, PickEventEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(TimeConverters::class)
abstract class PickerDatabase : RoomDatabase() {
    abstract fun pickerDao(): PickerDao
    abstract fun skuDao(): SkuDao
    abstract fun cellDao(): CellDao
    abstract fun batchDao(): BatchDao
    abstract fun orderDao(): OrderDao
    abstract fun pickItemDao(): PickItemDao
    abstract fun boxDao(): BoxDao
    abstract fun eventOutboxDao(): EventOutboxDao
}
