package com.nojoom.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MaterialType { GOLD, SILVER, STONE }

@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sku: String,                 // e.g. NJ-NK-001
    val name: String,
    val category: String = "",       // Necklace, Ring, Bridal Accessories...
    val material: MaterialType = MaterialType.GOLD,
    val karat: Int = 21,             // 24, 21, or 18 for gold; ignored for silver/stone
    val weightGrams: Double = 0.0,
    val quantity: Int = 1,
    val photoPath: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
