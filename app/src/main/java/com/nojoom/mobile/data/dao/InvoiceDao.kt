package com.nojoom.mobile.data.dao

import androidx.room.*
import com.nojoom.mobile.data.entity.Invoice
import com.nojoom.mobile.data.entity.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getById(id: Long): Invoice?

    @Query("SELECT * FROM invoices WHERE orderId = :orderId")
    suspend fun getByOrder(orderId: Long): Invoice?

    @Insert
    suspend fun insert(invoice: Invoice): Long

    @Update
    suspend fun update(invoice: Invoice)

    @Delete
    suspend fun delete(invoice: Invoice)

    @Query("SELECT COUNT(*) FROM invoices")
    suspend fun count(): Int
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId ORDER BY paidAt DESC")
    fun getForInvoice(invoiceId: Long): Flow<List<Payment>>

    @Insert
    suspend fun insert(payment: Payment): Long

    @Delete
    suspend fun delete(payment: Payment)
}
