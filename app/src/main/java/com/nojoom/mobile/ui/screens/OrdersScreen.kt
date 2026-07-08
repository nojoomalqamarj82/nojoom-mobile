package com.nojoom.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nojoom.mobile.ui.AppViewModel
import com.nojoom.mobile.ui.nav.Routes

@Composable
fun OrdersScreen(vm: AppViewModel, nav: NavController) {
    val orders by vm.orders.collectAsState()
    val clients by vm.clients.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate(Routes.NEW_ORDER) }) {
                Icon(Icons.Filled.Add, contentDescription = "New order")
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            items(orders) { order ->
                val clientName = clients.find { it.id == order.clientId }?.name ?: "—"
                ListItem(
                    headlineContent = { Text(order.orderRef) },
                    supportingContent = { Text("$clientName · ${order.status.name}") },
                    modifier = Modifier.clickableRow { nav.navigate(Routes.orderDetail(order.id)) }
                )
                HorizontalDivider()
            }
        }
    }
}
