package com.nojoom.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nojoom.mobile.data.entity.Client
import com.nojoom.mobile.ui.AppViewModel
import com.nojoom.mobile.ui.nav.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(vm: AppViewModel, nav: NavController) {
    val clients by vm.clients.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Filled.Add, contentDescription = "Add client") }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            items(clients) { client ->
                ListItem(
                    headlineContent = { Text(client.name) },
                    supportingContent = { Text(listOfNotNull(client.phone.ifBlank { null }, client.country.ifBlank { null }).joinToString(" · ")) },
                    modifier = Modifier.clickableRow { nav.navigate(Routes.clientDetail(client.id)) }
                )
                HorizontalDivider()
            }
        }
    }

    if (showAdd) {
        ClientEditDialog(onDismiss = { showAdd = false }) { name, phone, country, rep ->
            vm.saveClient(Client(name = name, phone = phone, country = country, repName = rep))
            showAdd = false
        }
    }
}

@Composable
fun ClientDetailScreen(vm: AppViewModel, clientId: Long, nav: NavController) {
    val clients by vm.clients.collectAsState()
    val orders by vm.orders.collectAsState()
    val client = clients.find { it.id == clientId }
    var editing by remember { mutableStateOf(false) }

    if (client == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { Text("Client not found") }
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(client.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(client.phone, style = MaterialTheme.typography.bodyMedium)
        Text(client.country, style = MaterialTheme.typography.bodyMedium)
        Text("Rep: ${client.repName.ifBlank { "—" }}", style = MaterialTheme.typography.bodyMedium)
        if (client.notes.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(client.notes, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))
        Row {
            OutlinedButton(onClick = { editing = true }) { Text("Edit") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { vm.deleteClient(client); nav.popBackStack() }) { Text("Delete") }
        }
        Spacer(Modifier.height(20.dp))
        Text("Orders", style = MaterialTheme.typography.titleMedium)
        orders.filter { it.clientId == clientId }.forEach { order ->
            ListItem(
                headlineContent = { Text(order.orderRef) },
                supportingContent = { Text(order.status.name) },
                modifier = Modifier.clickableRow { nav.navigate(Routes.orderDetail(order.id)) }
            )
        }
    }

    if (editing) {
        ClientEditDialog(initial = client, onDismiss = { editing = false }) { name, phone, country, rep ->
            vm.saveClient(client.copy(name = name, phone = phone, country = country, repName = rep))
            editing = false
        }
    }
}

@Composable
private fun ClientEditDialog(
    initial: Client? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, country: String, rep: String) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var phone by remember { mutableStateOf(initial?.phone ?: "") }
    var country by remember { mutableStateOf(initial?.country ?: "") }
    var rep by remember { mutableStateOf(initial?.repName ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New Client" else "Edit Client") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone / WhatsApp") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = rep, onValueChange = { rep = it }, label = { Text("Rep name") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onSave(name, phone, country, rep) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
