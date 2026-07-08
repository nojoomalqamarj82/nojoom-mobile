package com.nojoom.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus { PENDING, CONFIRMED, DELIVERED, CANCELLED }

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderRef: String,            // readable NJ-0001 style sequential ref
    val clientId: Long,
    val repName: String = "",
    val status: OrderStatus = OrderStatus.PENDING,
    val goldRate24k: Double = 0.0,   // rate/gram at time of order, for cost calc
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val inventoryItemId: Long? = null,
    val description: String,
    val material: MaterialType = MaterialType.GOLD,
    val karat: Int = 21,
    val weightGrams: Double = 0.0,
    val silverCostPerGram: Double = 0.0, // used when material = SILVER
    val stoneCost: Double = 0.0,         // used when material = STONE
    val salePrice: Double = 0.0
)
