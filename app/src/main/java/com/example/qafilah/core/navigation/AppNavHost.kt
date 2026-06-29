package com.example.qafilah.core.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.presentation.screens.LoginScreen
import com.example.qafilah.features.cart.presentation.CartScreen
import com.example.qafilah.features.onboarding.OnboardingScreen
import com.example.qafilah.features.splash.SplashScreen
import com.example.qafilah.features.wishlist.WishlistScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {


        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = { _: AppUser ->
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onContinueAsGuest = {
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {

        }


        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
                ?: return@composable
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Product: $productId")
            }
        }

        composable(Screen.Checkout.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Checkout")
            }
        }

        composable(
            route = Screen.OrderConfirmation.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
                ?: return@composable
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Order: $orderId")
            }
        }


        composable(NavItem.Home.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Home")
            }
        }

        composable(NavItem.Search.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Search")
            }
        }


        composable(NavItem.Cart.route) {
            CartScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToSignUp = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = {
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(NavItem.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavItem.Wishlist.route) {
            WishlistScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToSignUp = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = {
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(NavItem.Wishlist.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavItem.Profile.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Profile")
            }
        }
    }
}