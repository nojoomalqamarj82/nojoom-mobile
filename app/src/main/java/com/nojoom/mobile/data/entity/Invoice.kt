package com.nojoom.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class InvoiceStatus { UNPAID, PARTIAL, PAID, VOIDED }

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceRef: String,
    val orderId: Long,
    val clientId: Long,
    val totalAmount: Double = 0.0,
    val amountPaid: Double = 0.0,
    val status: InvoiceStatus = InvoiceStatus.UNPAID,
    // preserved for reversible void, mirrors desktop cascade fix (status_before_void)
    val statusBeforeVoid: InvoiceStatus? = null,
    val createdAt: Long = System.currentTimeMillis()
)
