package com.picker.app.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "picker", indices = [Index(value = ["login"], unique = true)])
data class PickerEntity(
    @PrimaryKey val id: String,
    val login: String,
    val pinHash: String,
    val fullName: String,
)

@Entity(tableName = "sku", indices = [Index(value = ["ean13"], unique = true)])
data class SkuEntity(
    @PrimaryKey val id: String,
    val ean13: String,
    val name: String,
    val tempZone: String,
    val grossWeightG: Int,
    val unit: String,
)

@Entity(tableName = "cell", indices = [Index(value = ["code"], unique = true)])
data class CellEntity(
    @PrimaryKey val id: String,
    val code: String,
    val zone: String,
    val rowNo: Short,
    val level: Short,
)
