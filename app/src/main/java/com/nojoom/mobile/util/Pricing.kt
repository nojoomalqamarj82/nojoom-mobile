package com.nojoom.mobile.util

import com.nojoom.mobile.data.entity.MaterialType
import com.nojoom.mobile.data.entity.OrderItem

object Pricing {
    /**
     * Mirrors Nojoom Manager desktop formula:
     * Pure Gold Weight = Weight × Karat/24
     * Gold Cost = Pure Gold × 24K Rate
     * Profit = Price − Total Cost
     */
    fun costOf(item: OrderItem, goldRate24k: Double): Double = when (item.material) {
        MaterialType.GOLD -> {
            val pureGoldWeight = item.weightGrams * (item.karat / 24.0)
            pureGoldWeight * goldRate24k
        }
        MaterialType.SILVER -> item.weightGrams * item.silverCostPerGram
        MaterialType.STONE -> item.stoneCost
    }

    fun profitOf(item: OrderItem, goldRate24k: Double): Double =
        item.salePrice - costOf(item, goldRate24k)

    fun pureGoldWeight21kEquivalent(item: OrderItem): Double =
        if (item.material == MaterialType.GOLD) item.weightGrams * (item.karat / 21.0) else 0.0
}
