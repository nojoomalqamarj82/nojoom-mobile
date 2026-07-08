package com.nojoom.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "representatives")
data class Representative(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val region: String = "",           // Comoros, France, Mayotte
    // Reward counter: grams of 21K-equivalent gold sold since last reward payout.
    // 30g reached => €100 reward, then counter resets to 0.
    val rewardCounterGrams21k: Double = 0.0,
    val totalRewardsEarnedEur: Double = 0.0
)

const val REWARD_THRESHOLD_GRAMS_21K = 30.0
const val REWARD_AMOUNT_EUR = 100.0
