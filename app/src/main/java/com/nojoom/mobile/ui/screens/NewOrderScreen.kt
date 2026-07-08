package com.nojoom.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nojoom.mobile.ui.AppViewModel
import com.nojoom.mobile.ui.nav.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(vm: AppViewModel, nav: NavController) {
    val clients by vm.clients.collectAsState()
    val reps by vm.representatives.collectAsState()

    var clientExpanded by remember { mutableStateOf(false) }
    var selectedClientId by remember { mutableStateOf<Long?>(null) }
    var repExpanded by remember { mutableStateOf(false) }
    var selectedRep by remember { mutableStateOf("") }
    var goldRate by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("New Order", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = clientExpanded, onExpandedChange = { clientExpanded = it }) {
            OutlinedTextField(
                value = clients.find { it.id == selectedClientId }?.name ?: "Select client",
                onValueChange = {}, readOnly = true, label = { Text("Client") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = clientExpanded, onDismissRequest = { clientExpanded = false }) {
                clients.forEach { c ->
                    DropdownMenuItem(text = { Text(c.name) }, onClick = { selectedClientId = c.id; clientExpanded = false })
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = repExpanded, onExpandedChange = { repExpanded = it }) {
            OutlinedTextField(
                value = selectedRep.ifBlank { "Select rep" },
                onValueChange = {}, readOnly = true, label = { Text("Representative") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = repExpanded, onDismissRequest = { repExpanded = false }) {
                reps.forEach { r ->
                    DropdownMenuItem(text = { Text("${r.name} (${r.region})") }, onClick = { selectedRep = r.name; repExpanded = false })
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = goldRate, onValueChange = { goldRate = it },
            label = { Text("24K gold rate today (per gram)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val rate = goldRate.toDoubleOrNull() ?: 0.0
                val cid = selectedClientId ?: return@Button
                vm.createOrder(cid, selectedRep, rate) { newId ->
                    nav.navigate(Routes.orderDetail(newId)) {
                        popUpTo(Routes.NEW_ORDER) { inclusive = true }
                    }
                }
            },
            enabled = selectedClientId != null,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Create Order") }
    }
}
