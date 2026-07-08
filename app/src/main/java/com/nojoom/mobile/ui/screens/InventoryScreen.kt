package com.nojoom.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nojoom.mobile.data.entity.InventoryItem
import com.nojoom.mobile.data.entity.MaterialType
import com.nojoom.mobile.ui.AppViewModel
import com.nojoom.mobile.ui.nav.Routes

@Composable
fun InventoryScreen(vm: AppViewModel, nav: NavController) {
    val inventory by vm.inventory.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Filled.Add, contentDescription = "Add item") }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            items(inventory) { item ->
                ListItem(
                    headlineContent = { Text("${item.sku} · ${item.name}") },
                    supportingContent = { Text("${item.material} · ${item.weightGrams}g · Qty ${item.quantity}") },
                    modifier = Modifier.clickableRow { nav.navigate(Routes.inventoryDetail(item.id)) }
                )
                HorizontalDivider()
            }
        }
    }

    if (showAdd) {
        InventoryEditDialog(onDismiss = { showAdd = false }) { item -> vm.saveInventoryItem(item); showAdd = false }
    }
}

@Composable
fun InventoryDetailScreen(vm: AppViewModel, itemId: Long, nav: NavController) {
    val inventory by vm.inventory.collectAsState()
    val item = inventory.find { it.id == itemId }
    var editing by remember { mutableStateOf(false) }

    if (item == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Item not found") }
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(item.name, style = MaterialTheme.typography.headlineMedium)
        Text(item.sku, style = MaterialTheme.typography.bodyMedium)
        Text(item.category.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium)
        Text("${item.material} · ${item.weightGrams}g" + if (item.material == MaterialType.GOLD) " · ${item.karat}K" else "")
        Text("Quantity: ${item.quantity}")
        if (item.notes.isNotBlank()) Text(item.notes)

        Spacer(Modifier.height(16.dp))
        Row {
            OutlinedButton(onClick = { editing = true }) { Text("Edit") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { vm.deleteInventoryItem(item); nav.popBackStack() }) { Text("Delete") }
        }
    }

    if (editing) {
        InventoryEditDialog(initial = item, onDismiss = { editing = false }) { updated -> vm.saveInventoryItem(updated); editing = false }
    }
}

@Composable
private fun InventoryEditDialog(initial: InventoryItem? = null, onDismiss: () -> Unit, onSave: (InventoryItem) -> Unit) {
    var sku by remember { mutableStateOf(initial?.sku ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var material by remember { mutableStateOf(initial?.material ?: MaterialType.GOLD) }
    var karat by remember { mutableStateOf((initial?.karat ?: 21).toString()) }
    var weight by remember { mutableStateOf((initial?.weightGrams ?: 0.0).toString()) }
    var quantity by remember { mutableStateOf((initial?.quantity ?: 1).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New Inventory Item" else "Edit Item") },
        text = {
            Column {
                OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("SKU (e.g. NJ-NK-033)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MaterialType.entries.forEach { m ->
                        FilterChip(selected = material == m, onClick = { material = m }, label = { Text(m.name) })
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (material == MaterialType.GOLD) {
                    OutlinedTextField(value = karat, onValueChange = { karat = it }, label = { Text("Karat") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (g)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    (initial ?: InventoryItem(sku = "", name = "")).copy(
                        sku = sku, name = name, category = category, material = material,
                        karat = karat.toIntOrNull() ?: 21, weightGrams = weight.toDoubleOrNull() ?: 0.0,
                        quantity = quantity.toIntOrNull() ?: 1
                    )
                )
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
