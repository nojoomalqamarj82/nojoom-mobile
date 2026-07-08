package com.nojoom.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.nojoom.mobile.ui.AppViewModel
import com.nojoom.mobile.ui.nav.Routes
import com.nojoom.mobile.ui.screens.*
import com.nojoom.mobile.ui.theme.NojoomMobileTheme

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NojoomMobileTheme {
                NojoomApp(vm)
            }
        }
    }
}

private data class NavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomItems = listOf(
    NavItem(Routes.DASHBOARD, "Dashboard", Icons.Filled.Dashboard),
    NavItem(Routes.CLIENTS, "Clients", Icons.Filled.People),
    NavItem(Routes.ORDERS, "Orders", Icons.Filled.ShoppingBag),
    NavItem(Routes.INVENTORY, "Stock", Icons.Filled.Diamond),
    NavItem(Routes.INVOICES, "Invoices", Icons.Filled.Receipt),
)

@Composable
fun NojoomApp(vm: AppViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) { DashboardScreen(vm, navController) }
            composable(Routes.CLIENTS) { ClientsScreen(vm, navController) }
            composable(Routes.CLIENT_DETAIL) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("clientId")?.toLongOrNull() ?: 0L
                ClientDetailScreen(vm, id, navController)
            }
            composable(Routes.ORDERS) { OrdersScreen(vm, navController) }
            composable(Routes.NEW_ORDER) { NewOrderScreen(vm, navController) }
            composable(Routes.ORDER_DETAIL) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("orderId")?.toLongOrNull() ?: 0L
                OrderDetailScreen(vm, id, navController)
            }
            composable(Routes.INVENTORY) { InventoryScreen(vm, navController) }
            composable(Routes.INVENTORY_DETAIL) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("itemId")?.toLongOrNull() ?: 0L
                InventoryDetailScreen(vm, id, navController)
            }
            composable(Routes.INVOICES) { InvoicesScreen(vm, navController) }
            composable(Routes.INVOICE_DETAIL) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("invoiceId")?.toLongOrNull() ?: 0L
                InvoiceDetailScreen(vm, id)
            }
            composable(Routes.REPS) { RepsScreen(vm) }
        }
    }
}
