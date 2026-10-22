package com.senorita.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.senorita.app.ui.screens.auth.LoginScreen
import com.senorita.app.ui.screens.auth.AuthRequiredScreen
import com.senorita.app.ui.screens.auth.OtpVerificationScreen
import com.senorita.app.ui.screens.auth.RegisterScreen
import com.senorita.app.ui.screens.cart.CartScreen
import com.senorita.app.ui.screens.home.HomeScreen
import com.senorita.app.ui.screens.menu.MenuDetailScreen
import com.senorita.app.ui.screens.menu.MenuListScreen
import com.senorita.app.ui.screens.notification.NotificationScreen
import com.senorita.app.ui.screens.onboarding.OnboardingScreen
import com.senorita.app.ui.screens.order.*
import com.senorita.app.ui.screens.outlet.OutletLocationScreen
import com.senorita.app.ui.screens.profile.EditProfileScreen
import com.senorita.app.ui.screens.profile.ProfileScreen
import com.senorita.app.ui.screens.rewards.RewardsScreen
import com.senorita.app.ui.screens.splash.SplashScreen
import com.senorita.app.ui.viewmodel.*

@Composable
fun SenoritaNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    rewardsViewModel: RewardsViewModel,
    profileViewModel: ProfileViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val navigateToHome = {
        val removedExistingHome = navController.popBackStack(Screen.Home.route, inclusive = true)
        if (removedExistingHome) {
            navController.navigate(Screen.Home.route) { launchSingleTop = true }
        } else {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    val openAuthFromGate: (String) -> Unit = { route ->
        authViewModel.isGuest = false
        navController.navigate(route) {
            popUpTo(Screen.AuthRequired.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            SenoritaBottomBar(
                navController = navController,
                cartItemCount = cartItems.sumOf { it.quantity },
                isGuest = authViewModel.isGuest
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    isLoggedIn = profileViewModel.userProfile.name.isNotBlank(),
                    onNavigateNext = { isLoggedIn ->
                        if (isLoggedIn) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Otp.route)
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onContinueAsGuest = {
                        authViewModel.isGuest = true
                        navigateToHome()
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onNavigateToOtp = { navController.navigate(Screen.Otp.route) },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(Screen.Otp.route) {
                OtpVerificationScreen(
                    authViewModel = authViewModel,
                    onOtpSuccess = {
                        authViewModel.isGuest = false
                        profileViewModel.loadProfileForEdit()
                        navigateToHome()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.AuthRequired.route) {
                AuthRequiredScreen(
                    onBackClick = { navController.popBackStack() },
                    onLoginClick = { openAuthFromGate(Screen.Login.route) },
                    onRegisterClick = { openAuthFromGate(Screen.Register.route) }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    menuViewModel = menuViewModel,
                    profileViewModel = profileViewModel,
                    onNavigateToMenu = { navController.navigate(Screen.MenuList.route) },
                    onNavigateToDetail = { itemId ->
                        navController.navigate(Screen.MenuDetail.createRoute(itemId))
                    },
                    onNavigateToOutletLocation = { navController.navigate(Screen.OutletLocation.route) },
                    onNavigateToRewards = { navController.navigate(Screen.Rewards.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notification.route) }
                )
            }

            composable(Screen.MenuList.route) {
                MenuListScreen(
                    menuViewModel = menuViewModel,
                    onNavigateToDetail = { itemId ->
                        navController.navigate(Screen.MenuDetail.createRoute(itemId))
                    }
                )
            }

            composable(
                route = Screen.MenuDetail.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                MenuDetailScreen(
                    itemId = itemId,
                    menuViewModel = menuViewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddToCartSuccess = {
                        navController.navigate(Screen.Cart.route)
                    }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    cartViewModel = cartViewModel,
                    onNavigateToChooseOutlet = { navController.navigate(Screen.ChooseOutlet.route) },
                    onNavigateToMenu = { navController.navigate(Screen.MenuList.route) }
                )
            }

            composable(Screen.ChooseOutlet.route) {
                ChooseOutletScreen(
                    orderViewModel = orderViewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                    onNavigateToPayment = { navController.navigate(Screen.Payment.route) }
                )
            }

            composable(Screen.ScanQr.route) {
                ScanQrScreen(
                    orderViewModel = orderViewModel,
                    onBackClick = { navController.popBackStack() },
                    onScanComplete = {
                        navController.popBackStack()
                        navController.navigate(Screen.Payment.route)
                    }
                )
            }

            composable(Screen.Payment.route) {
                PaymentScreen(
                    cartViewModel = cartViewModel,
                    orderViewModel = orderViewModel,
                    onBackClick = { navController.popBackStack() },
                    onPaymentSuccess = { newOrderId ->
                        navController.navigate(Screen.OrderTracking.createRoute(newOrderId)) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                )
            }

            composable(
                route = Screen.OrderTracking.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderTrackingScreen(
                    orderId = orderId,
                    orderViewModel = orderViewModel,
                    onBackClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToReceipt = { id ->
                        navController.navigate(Screen.OnlineReceipt.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.OnlineReceipt.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OnlineReceiptScreen(
                    orderId = orderId,
                    orderViewModel = orderViewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.OrderHistory.route) {
                OrderHistoryScreen(
                    orderViewModel = orderViewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToTracking = { id -> navController.navigate(Screen.OrderTracking.createRoute(id)) },
                    onNavigateToReceipt = { id -> navController.navigate(Screen.OnlineReceipt.createRoute(id)) }
                )
            }

            composable(Screen.Rewards.route) {
                if (authViewModel.isGuest) {
                    AuthRequiredScreen(
                        onBackClick = { navController.popBackStack() },
                        onLoginClick = { openAuthFromGate(Screen.Login.route) },
                        onRegisterClick = { openAuthFromGate(Screen.Register.route) }
                    )
                } else {
                    RewardsScreen(
                        rewardsViewModel = rewardsViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.OutletLocation.route) {
                OutletLocationScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                if (authViewModel.isGuest) {
                    AuthRequiredScreen(
                        onBackClick = { navController.popBackStack() },
                        onLoginClick = { openAuthFromGate(Screen.Login.route) },
                        onRegisterClick = { openAuthFromGate(Screen.Register.route) }
                    )
                } else ProfileScreen(
                    profileViewModel = profileViewModel,
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                    onNavigateToOrders = { navController.navigate(Screen.OrderHistory.route) },
                    onNavigateToRewards = { navController.navigate(Screen.Rewards.route) },
                    onNavigateToOutletLocation = { navController.navigate(Screen.OutletLocation.route) },
                    onLogoutSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    profileViewModel = profileViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable(Screen.Notification.route) {
                NotificationScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
