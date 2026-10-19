package com.senorita.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.compose.rememberNavController
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.fragments.OnboardingFragment
import com.senorita.app.ui.fragments.SplashFragment
import com.senorita.app.ui.navigation.SenoritaNavGraph
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.*

class MainActivity : AppCompatActivity() {

    private lateinit var composeView: ComposeView
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var repository: SenoritaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        composeView = findViewById(R.id.compose_view)
        preferencesManager = PreferencesManager(applicationContext)
        repository = SenoritaRepository(preferencesManager)

        showSplashFragment()
    }

    private fun showSplashFragment() {
        val splashFragment = SplashFragment(
            onSplashComplete = {
                if (preferencesManager.isLoggedIn) {
                    showMainComposeContent()
                } else {
                    showOnboardingFragment()
                }
            }
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, splashFragment)
            .commitAllowingStateLoss()
    }

    private fun showOnboardingFragment() {
        val onboardingFragment = OnboardingFragment(
            onOnboardingComplete = {
                showMainComposeContent()
            }
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, onboardingFragment)
            .commitAllowingStateLoss()
    }

    private fun showMainComposeContent() {
        findViewById<View>(R.id.main_fragment_container).visibility = View.GONE
        composeView.visibility = View.VISIBLE

        val authViewModel = AuthViewModel(preferencesManager)
        val menuViewModel = MenuViewModel(repository)
        val cartViewModel = CartViewModel(repository)
        val orderViewModel = OrderViewModel(repository)
        val rewardsViewModel = RewardsViewModel(repository)
        val profileViewModel = ProfileViewModel(repository)

        composeView.setContent {
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
