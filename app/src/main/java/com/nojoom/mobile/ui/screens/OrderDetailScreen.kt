package com.nojoom.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nojoom.mobile.data.entity.MaterialType
import com.nojoom.mobile.data.entity.OrderItem
import com.nojoom.mobile.data.entity.OrderStatus
import com.nojoom.mobile.ui.AppViewModel
import com.nojoom.mobile.ui.nav.Routes
import com.nojoom.mobile.ui.theme.NojoomGold
import com.nojoom.mobile.util.Pricing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(vm: AppViewModel, orderId: Long, nav: NavController) {
    val orders by vm.orders.collectAsState()
    val clients by vm.clients.collectAsState()
    val order = orders.find { it.id == orderId } ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Order not found") }
        return
    }
    val items by vm.itemsForOrder(orderId).collectAsState(initial = emptyList())
    val client = clients.find { it.id == order.clientId }
    var showAddItem by remember { mutableStateOf(false) }

    val totalSale = items.sumOf { it.salePrice }
    val totalCost = items.sumOf { Pricing.costOf(it, order.goldRate24k) }
    val totalProfit = totalSale - totalCost

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(order.orderRef, style = MaterialTheme.typography.headlineMedium, color = NojoomGold)
        Text(client?.name ?: "—", style = MaterialTheme.typography.bodyMedium)
        Text("Rep: ${order.repName.ifBlank { "—" }} · Rate/g (24K): ${order.goldRate24k}", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrderStatus.entries.forEach { status ->
                FilterChip(
                    selected = order.status == status,
                    onClick = { /* status change handled via VM in a full build */ },
                    label = { Text(status.name) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Items", style = MaterialTheme.typography.titleMedium)
        items.forEach { item ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text(item.description, style = MaterialTheme.typography.titleMedium)
                    Text("${item.material} · ${item.weightGrams}g" + if (item.material == MaterialType.GOLD) " · ${item.karat}K" else "")
                    Text("Sale: %.2f  Cost: %.2f  Profit: %.2f".format(
                        item.salePrice, Pricing.costOf(item, order.goldRate24k), Pricing.profitOf(item, order.goldRate24k)
                    ))
                }
            }
        }
        OutlinedButton(onClick = { showAddItem = true }, modifier = Modifier.fillMaxWidth()) { Text("+ Add Item") }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        Text("Total Sale: %.2f".format(totalSale))
        Text("Total Cost: %.2f".format(totalCost))
        Text("Total Profit: %.2f".format(totalProfit), color = NojoomGold)

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                vm.generateInvoice(order, items) { invId -> nav.navigate(Routes.invoiceDetail(invId)) }
            },
            enabled = items.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Generate Invoice") }
    }

    if (showAddItem) {
        AddOrderItemDialog(onDismiss = { showAddItem = false }) { item ->
            vm.saveOrderItem(item.copy(orderId = orderId))
            showAddItem = false
        }
    }
}

@Composable
private fun AddOrderItemDialog(onDismiss: () -> Unit, onSave: (OrderItem) -> Unit) {
    var description by remember { mutableStateOf("") }
    var material by remember { mutableStateOf(MaterialType.GOLD) }
    var karat by remember { mutableStateOf("21") }
    var weight by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            Column {
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MaterialType.entries.forEach { m ->
                        FilterChip(selected = material == m, onClick = { material = m }, label = { Text(m.name) })
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (material == MaterialType.GOLD) {
                    OutlinedTextField(value = karat, onValueChange = { karat = it }, label = { Text("Karat (24/21/18)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (g)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Sale price") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    OrderItem(
                        orderId = 0,
                        description = description,
                        material = material,
                        karat = karat.toIntOrNull() ?: 21,
                        weightGrams = weight.toDoubleOrNull() ?: 0.0,
                        salePrice = price.toDoubleOrNull() ?: 0.0
                    )
                )
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
