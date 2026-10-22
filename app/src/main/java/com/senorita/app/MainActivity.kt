package com.senorita.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.navigation.SenoritaNavGraph
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.AuthViewModel
import com.senorita.app.ui.viewmodel.CartViewModel
import com.senorita.app.ui.viewmodel.MenuViewModel
import com.senorita.app.ui.viewmodel.OrderViewModel
import com.senorita.app.ui.viewmodel.ProfileViewModel
import com.senorita.app.ui.viewmodel.RewardsViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = PreferencesManager(applicationContext)
        val repository = SenoritaRepository(preferencesManager)
        val authViewModel = AuthViewModel(preferencesManager)
        val menuViewModel = MenuViewModel(repository)
        val cartViewModel = CartViewModel(repository)
        val orderViewModel = OrderViewModel(repository)
        val rewardsViewModel = RewardsViewModel(repository)
        val profileViewModel = ProfileViewModel(repository)

        setContent {
            SENORITATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    SenoritaNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        menuViewModel = menuViewModel,
                        cartViewModel = cartViewModel,
                        orderViewModel = orderViewModel,
                        rewardsViewModel = rewardsViewModel,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}
