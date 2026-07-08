package com.nojoom.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nojoom.mobile.ui.AppViewModel
import com.nojoom.mobile.ui.nav.Routes
import com.nojoom.mobile.ui.theme.NojoomGold

private data class Kpi(val label: String, val value: String)

@Composable
fun DashboardScreen(vm: AppViewModel, nav: NavController) {
    val clients by vm.clients.collectAsState()
    val orders by vm.orders.collectAsState()

    val kpis = listOf(
        Kpi("Clients", clients.size.toString()),
        Kpi("Orders", orders.size.toString()),
        Kpi("Pending Orders", vm.pendingOrders.toString()),
        Kpi("Revenue Collected", "AED %.0f".format(vm.totalRevenue)),
        Kpi("Outstanding", "AED %.0f".format(vm.outstandingBalance)),
    )

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        HeroBanner(title = "Nojoom Al Qamar", subtitle = "Mobile Companion")
        Spacer(Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(kpis) { kpi -> KpiCard(kpi) }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { nav.navigate(Routes.REPS) }, modifier = Modifier.fillMaxWidth()) {
            Text("View Representatives & Rewards")
        }

        Spacer(Modifier.height(20.dp))
        Text("Recent Orders", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        orders.take(5).forEach { order ->
            ListItem(
                headlineContent = { Text(order.orderRef) },
                supportingContent = { Text(order.status.name) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Composable
private fun KpiCard(kpi: Kpi) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text(kpi.value, style = MaterialTheme.typography.titleLarge, color = NojoomGold, fontWeight = FontWeight.Bold)
            Text(kpi.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
