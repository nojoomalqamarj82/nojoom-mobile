package com.nojoom.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val amount: Double,
    val method: String = "",     // Cash, Bank Transfer, ADIB, etc.
    val note: String = "",
    val paidAt: Long = System.currentTimeMillis()
)
