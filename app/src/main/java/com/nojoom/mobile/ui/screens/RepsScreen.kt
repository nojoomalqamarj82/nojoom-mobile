package com.nojoom.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nojoom.mobile.data.entity.REWARD_THRESHOLD_GRAMS_21K
import com.nojoom.mobile.ui.AppViewModel
import com.nojoom.mobile.ui.theme.NojoomGold

@Composable
fun RepsScreen(vm: AppViewModel) {
    val reps by vm.representatives.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Representatives", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        reps.forEach { rep ->
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("${rep.name} · ${rep.region}", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (rep.rewardCounterGrams21k / REWARD_THRESHOLD_GRAMS_21K).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                        color = NojoomGold
                    )
                    Text("%.1fg / %.0fg toward next €100 reward".format(rep.rewardCounterGrams21k, REWARD_THRESHOLD_GRAMS_21K))
                    Text("Total earned: €%.0f".format(rep.totalRewardsEarnedEur))
                }
            }
        }
    }
}
