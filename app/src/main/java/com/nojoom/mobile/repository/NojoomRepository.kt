package com.nojoom.mobile.repository

import com.nojoom.mobile.data.AppDatabase
import com.nojoom.mobile.data.entity.*

class NojoomRepository(private val db: AppDatabase) {

    // Clients
    fun clients() = db.clientDao().getAll()
    fun searchClients(q: String) = db.clientDao().search(q)
    suspend fun getClient(id: Long) = db.clientDao().getById(id)
    suspend fun saveClient(c: Client) = if (c.id == 0L) db.clientDao().insert(c) else { db.clientDao().update(c); c.id }
    suspend fun deleteClient(c: Client) = db.clientDao().delete(c)

    // Orders
    fun orders() = db.orderDao().getAll()
    suspend fun getOrder(id: Long) = db.orderDao().getById(id)
    fun itemsForOrder(orderId: Long) = db.orderDao().getItemsForOrder(orderId)
    suspend fun saveOrder(o: Order) = if (o.id == 0L) db.orderDao().insert(o) else { db.orderDao().update(o); o.id }
    suspend fun saveOrderItem(i: OrderItem) = if (i.id == 0L) db.orderDao().insertItem(i) else { db.orderDao().updateItem(i); i.id }
    suspend fun deleteOrderItem(i: OrderItem) = db.orderDao().deleteItem(i)
    suspend fun nextOrderRef(): String {
        val n = db.orderDao().count() + 1
        return "NJ-%04d".format(n)
    }

    // Inventory
    fun inventory() = db.inventoryDao().getAll()
    fun searchInventory(q: String) = db.inventoryDao().search(q)
    suspend fun saveInventoryItem(i: InventoryItem) = if (i.id == 0L) db.inventoryDao().insert(i) else { db.inventoryDao().update(i); i.id }
    suspend fun deleteInventoryItem(i: InventoryItem) = db.inventoryDao().delete(i)

    // Invoices & Payments
    fun invoices() = db.invoiceDao().getAll()
    suspend fun saveInvoice(inv: Invoice) = if (inv.id == 0L) db.invoiceDao().insert(inv) else { db.invoiceDao().update(inv); inv.id }
    suspend fun nextInvoiceRef(): String {
        val n = db.invoiceDao().count() + 1
        return "INV-%04d".format(n)
    }
    fun paymentsForInvoice(invoiceId: Long) = db.paymentDao().getForInvoice(invoiceId)
    suspend fun addPayment(p: Payment) = db.paymentDao().insert(p)

    /** Voids an invoice while preserving prior status for reversibility (mirrors desktop cascade fix). */
    suspend fun voidInvoice(inv: Invoice) {
        db.invoiceDao().update(inv.copy(statusBeforeVoid = inv.status, status = InvoiceStatus.VOIDED))
    }
    suspend fun unvoidInvoice(inv: Invoice) {
        val restored = inv.statusBeforeVoid ?: InvoiceStatus.UNPAID
        db.invoiceDao().update(inv.copy(status = restored, statusBeforeVoid = null))
    }

    // Representatives
    fun representatives() = db.representativeDao().getAll()
    suspend fun updateRepReward(rep: Representative) = db.representativeDao().update(rep)
}
