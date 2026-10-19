package com.senorita.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.senorita.app.data.local.MockData
import com.senorita.app.data.model.Reward
import com.senorita.app.data.repository.SenoritaRepository

class RewardsViewModel(private val repository: SenoritaRepository) : ViewModel() {

    var pointsBalance by mutableStateOf(repository.getPreferencesManager().getUserPoints())
    var redemptionMessage by mutableStateOf<String?>(null)

    fun refreshPoints() {
        pointsBalance = repository.getPreferencesManager().getUserPoints()
    }

    fun redeemReward(reward: Reward): Boolean {
        return if (repository.getPreferencesManager().deductPoints(reward.pointsCost)) {
            repository.applyVoucher(reward)
            refreshPoints()
            redemptionMessage = "Berhasil menukarkan ${reward.pointsCost} poin untuk ${reward.title}!"
            true
        } else {
            redemptionMessage = "Poin tidak mencukupi untuk menukarkan voucher ini."
            false
        }
    }

    fun getAvailableRewards(): List<Reward> = MockData.rewards

    fun getPointTransactions() = MockData.pointTransactions
}
