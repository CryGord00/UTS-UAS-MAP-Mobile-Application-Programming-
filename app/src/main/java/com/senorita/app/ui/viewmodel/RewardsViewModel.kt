package com.senorita.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.senorita.app.data.local.MockData
import com.senorita.app.data.model.PointTransaction
import com.senorita.app.data.model.Reward
import com.senorita.app.data.repository.SenoritaRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RewardsViewModel(private val repository: SenoritaRepository) : ViewModel() {

    var pointsBalance by mutableStateOf(repository.getPreferencesManager().getUserPoints())
    var redemptionMessage by mutableStateOf<String?>(null)

    val pointTransactions = mutableStateListOf<PointTransaction>().apply {
        addAll(MockData.pointTransactions)
    }

    fun refreshPoints() {
        pointsBalance = repository.getPreferencesManager().getUserPoints()
    }

    fun redeemReward(reward: Reward): Boolean {
        return if (repository.getPreferencesManager().deductPoints(reward.pointsCost)) {
            repository.applyVoucher(reward)
            refreshPoints()

            val currentDate = try {
                SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
            } catch (_: Exception) {
                "Hari ini"
            }

            val newTransaction = PointTransaction(
                id = "pt_${System.currentTimeMillis()}",
                title = "Penukaran ${reward.title}",
                points = reward.pointsCost,
                isEarned = false,
                date = currentDate
            )
            pointTransactions.add(0, newTransaction)

            redemptionMessage = "Berhasil menukarkan ${reward.pointsCost} poin untuk ${reward.title}!"
            true
        } else {
            redemptionMessage = "Poin tidak mencukupi untuk menukarkan voucher ini."
            false
        }
    }

    fun getAvailableRewards(): List<Reward> = MockData.rewards

    fun getPointTransactions(): List<PointTransaction> = pointTransactions
}
