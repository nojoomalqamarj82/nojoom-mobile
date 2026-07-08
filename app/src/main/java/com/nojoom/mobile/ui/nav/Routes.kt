package com.nojoom.mobile.ui.nav

object Routes {
    const val DASHBOARD = "dashboard"
    const val CLIENTS = "clients"
    const val CLIENT_DETAIL = "client/{clientId}"
    const val ORDERS = "orders"
    const val ORDER_DETAIL = "order/{orderId}"
    const val NEW_ORDER = "order/new"
    const val INVENTORY = "inventory"
    const val INVENTORY_DETAIL = "inventory/{itemId}"
    const val INVOICES = "invoices"
    const val INVOICE_DETAIL = "invoice/{invoiceId}"
    const val REPS = "reps"

    fun clientDetail(id: Long) = "client/$id"
    fun orderDetail(id: Long) = "order/$id"
    fun inventoryDetail(id: Long) = "inventory/$id"
    fun invoiceDetail(id: Long) = "invoice/$id"
}
