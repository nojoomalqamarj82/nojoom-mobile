package com.nojoom.mobile.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nojoom.mobile.data.AppDatabase
import com.nojoom.mobile.data.entity.*
import com.nojoom.mobile.repository.NojoomRepository
import com.nojoom.mobile.util.Pricing
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = NojoomRepository(AppDatabase.getInstance(app))

    val clients = repo.clients().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val orders = repo.orders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val inventory = repo.inventory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val invoices = repo.invoices().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val representatives = repo.representatives().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard KPIs
    val totalRevenue get() = invoices.value.sumOf { it.amountPaid }
    val outstandingBalance get() = invoices.value.filter { it.status != InvoiceStatus.VOIDED }
        .sumOf { it.totalAmount - it.amountPaid }
    val clientCount get() = clients.value.size
    val orderCount get() = orders.value.size
    val pendingOrders get() = orders.value.count { it.status == OrderStatus.PENDING }

    fun saveClient(c: Client, onDone: (Long) -> Unit = {}) = viewModelScope.launch {
        onDone(repo.saveClient(c))
    }
    fun deleteClient(c: Client) = viewModelScope.launch { repo.deleteClient(c) }

    fun saveInventoryItem(i: InventoryItem) = viewModelScope.launch { repo.saveInventoryItem(i) }
    fun deleteInventoryItem(i: InventoryItem) = viewModelScope.launch { repo.deleteInventoryItem(i) }

    fun createOrder(clientId: Long, repName: String, goldRate24k: Double, onDone: (Long) -> Unit = {}) =
        viewModelScope.launch {
            val ref = repo.nextOrderRef()
            val id = repo.saveOrder(Order(orderRef = ref, clientId = clientId, repName = repName, goldRate24k = goldRate24k))
            onDone(id)
        }

    fun saveOrderItem(item: OrderItem) = viewModelScope.launch { repo.saveOrderItem(item) }
    fun deleteOrderItem(item: OrderItem) = viewModelScope.launch { repo.deleteOrderItem(item) }

    fun itemsForOrder(orderId: Long) = repo.itemsForOrder(orderId)

    fun generateInvoice(order: Order, items: List<OrderItem>, onDone: (Long) -> Unit = {}) = viewModelScope.launch {
        val total = items.sumOf { it.salePrice }
        val ref = repo.nextInvoiceRef()
        val id = repo.saveInvoice(
            Invoice(invoiceRef = ref, orderId = order.id, clientId = order.clientId, totalAmount = total)
        )
        // Update rep reward counter using 21K-equivalent weight sold
        val rep = representatives.value.find { it.name == order.repName }
        if (rep != null) {
            val added = items.sumOf { Pricing.pureGoldWeight21kEquivalent(it) }
            var newCounter = rep.rewardCounterGrams21k + added
            var newTotal = rep.totalRewardsEarnedEur
            while (newCounter >= REWARD_THRESHOLD_GRAMS_21K) {
                newCounter -= REWARD_THRESHOLD_GRAMS_21K
                newTotal += REWARD_AMOUNT_EUR
            }
            repo.updateRepReward(rep.copy(rewardCounterGrams21k = newCounter, totalRewardsEarnedEur = newTotal))
        }
        onDone(id)
    }

    fun addPayment(invoice: Invoice, amount: Double, method: String) = viewModelScope.launch {
        repo.addPayment(Payment(invoiceId = invoice.id, amount = amount, method = method))
        val newPaid = invoice.amountPaid + amount
        val newStatus = when {
            newPaid >= invoice.totalAmount -> InvoiceStatus.PAID
            newPaid > 0 -> InvoiceStatus.PARTIAL
            else -> InvoiceStatus.UNPAID
        }
        repo.saveInvoice(invoice.copy(amountPaid = newPaid, status = newStatus))
    }

    fun paymentsForInvoice(invoiceId: Long) = repo.paymentsForInvoice(invoiceId)

    fun voidInvoice(inv: Invoice) = viewModelScope.launch { repo.voidInvoice(inv) }
}
