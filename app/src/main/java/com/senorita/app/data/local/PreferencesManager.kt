package com.senorita.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.senorita.app.data.model.PointTransaction
import com.senorita.app.data.model.UserProfile
import org.json.JSONArray
import org.json.JSONObject

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("senorita_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_POINTS = "user_points"
        private const val KEY_MEMBER_TIER = "member_tier"
        private const val KEY_IS_NEW_MEMBER = "is_new_member"
        private const val KEY_POINT_TRANSACTIONS = "point_transactions"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    fun getUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString(KEY_USER_NAME, "") ?: "",
            email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            phone = prefs.getString(KEY_USER_PHONE, "") ?: "",
            memberTier = prefs.getString(KEY_MEMBER_TIER, "Gold Member") ?: "Gold Member",
            pointsBalance = prefs.getInt(KEY_USER_POINTS, 1240)
        )
    }

    fun saveUserProfile(name: String, email: String, phone: String) {
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_PHONE, phone)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun getUserPoints(): Int {
        return prefs.getInt(KEY_USER_POINTS, 1240)
    }

    fun setUserPoints(points: Int) {
        prefs.edit().putInt(KEY_USER_POINTS, points).apply()
    }

    fun isNewMemberAccount(): Boolean {
        return prefs.getBoolean(KEY_IS_NEW_MEMBER, false)
    }

    fun setNewMemberAccount(isNewMember: Boolean) {
        prefs.edit()
            .putBoolean(KEY_IS_NEW_MEMBER, isNewMember)
            .remove(KEY_POINT_TRANSACTIONS)
            .apply()
    }

    fun getPointTransactions(): List<PointTransaction> {
        val savedTransactions = prefs.getString(KEY_POINT_TRANSACTIONS, null)
        if (savedTransactions.isNullOrBlank()) {
            return if (isNewMemberAccount()) {
                MockData.pointTransactions.filter { it.title == "Welcome Bonus New Member" }
            } else {
                MockData.pointTransactions
            }
        }

        return runCatching {
            val jsonArray = JSONArray(savedTransactions)
            List(jsonArray.length()) { index ->
                val item = jsonArray.getJSONObject(index)
                PointTransaction(
                    id = item.getString("id"),
                    title = item.getString("title"),
                    points = item.getInt("points"),
                    isEarned = item.getBoolean("isEarned"),
                    date = item.getString("date")
                )
            }
        }.getOrElse {
            if (isNewMemberAccount()) {
                MockData.pointTransactions.filter { it.title == "Welcome Bonus New Member" }
            } else {
                MockData.pointTransactions
            }
        }
    }

    fun savePointTransactions(transactions: List<PointTransaction>) {
        val jsonArray = JSONArray()
        transactions.forEach { transaction ->
            jsonArray.put(
                JSONObject()
                    .put("id", transaction.id)
                    .put("title", transaction.title)
                    .put("points", transaction.points)
                    .put("isEarned", transaction.isEarned)
                    .put("date", transaction.date)
            )
        }
        prefs.edit().putString(KEY_POINT_TRANSACTIONS, jsonArray.toString()).apply()
    }

    fun addPointTransaction(transaction: PointTransaction) {
        savePointTransactions(listOf(transaction) + getPointTransactions())
    }

    fun addPoints(amount: Int) {
        val current = getUserPoints()
        prefs.edit().putInt(KEY_USER_POINTS, current + amount).apply()
    }

    fun deductPoints(amount: Int): Boolean {
        val current = getUserPoints()
        if (current >= amount) {
            prefs.edit().putInt(KEY_USER_POINTS, current - amount).apply()
            return true
        }
        return false
    }

    fun logout() {
        prefs.edit().clear().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }
}
