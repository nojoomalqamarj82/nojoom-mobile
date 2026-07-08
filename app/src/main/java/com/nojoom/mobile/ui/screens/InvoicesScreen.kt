package com.nojoom.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nojoom.mobile.data.entity.InvoiceStatus
import com.nojoom.mobile.ui.AppViewModel
import com.nojoom.mobile.ui.nav.Routes
import com.nojoom.mobile.ui.theme.NojoomGold

@Composable
fun InvoicesScreen(vm: AppViewModel, nav: NavController) {
    val invoices by vm.invoices.collectAsState()
    LazyColumn(Modifier.fillMaxSize()) {
        items(invoices) { inv ->
            ListItem(
                headlineContent = { Text(inv.invoiceRef) },
                supportingContent = { Text("${inv.status.name} · Paid %.2f / %.2f".format(inv.amountPaid, inv.totalAmount)) },
                modifier = Modifier.clickableRow { nav.navigate(Routes.invoiceDetail(inv.id)) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun InvoiceDetailScreen(vm: AppViewModel, invoiceId: Long) {
    val invoices by vm.invoices.collectAsState()
    val invoice = invoices.find { it.id == invoiceId }
    var showPay by remember { mutableStateOf(false) }

    if (invoice == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Invoice not found") }
        return
    }
    val payments by vm.paymentsForInvoice(invoiceId).collectAsState(initial = emptyList())
    val balance = invoice.totalAmount - invoice.amountPaid

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(invoice.invoiceRef, style = MaterialTheme.typography.headlineMedium, color = NojoomGold)
        Text("Status: ${invoice.status.name}")
        Text("Total: %.2f".format(invoice.totalAmount))
        Text("Paid: %.2f".format(invoice.amountPaid))
        Text("Balance: %.2f".format(balance))

        Spacer(Modifier.height(16.dp))
        Row {
            if (invoice.status != InvoiceStatus.PAID && invoice.status != InvoiceStatus.VOIDED) {
                Button(onClick = { showPay = true }) { Text("Record Payment") }
                Spacer(Modifier.width(8.dp))
            }
            if (invoice.status != InvoiceStatus.VOIDED) {
                OutlinedButton(onClick = { vm.voidInvoice(invoice) }) { Text("Void") }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Payment history", style = MaterialTheme.typography.titleMedium)
        payments.forEach { p ->
            ListItem(
                headlineContent = { Text("%.2f".format(p.amount)) },
                supportingContent = { Text(p.method.ifBlank { "—" }) }
            )
        }
    }

    if (showPay) {
        RecordPaymentDialog(maxAmount = balance, onDismiss = { showPay = false }) { amount, method ->
            vm.addPayment(invoice, amount, method)
            showPay = false
        }
    }
}

@Composable
private fun RecordPaymentDialog(maxAmount: Double, onDismiss: () -> Unit, onSave: (Double, String) -> Unit) {
    var amount by remember { mutableStateOf(maxAmount.toString()) }
    var method by remember { mutableStateOf("Cash") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Payment") },
        text = {
            Column {
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = method, onValueChange = { method = it }, label = { Text("Method") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(amount.toDoubleOrNull() ?: 0.0, method) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
