package com.example.qafilah.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.presentation.screens.LoginScreen
import com.example.qafilah.features.auth.presentation.screens.SignUpScreen
import com.example.qafilah.features.cart.presentation.ui.CartScreen
import com.example.qafilah.features.home.presentation.HomeScreen
import com.example.qafilah.features.onboarding.OnboardingScreen
import com.example.qafilah.features.search.SearchScreen
import com.example.qafilah.features.search.domain.model.ChipState
import com.example.qafilah.features.splash.SplashScreen
import com.example.qafilah.features.wishlist.WishlistScreen
import com.example.qafilah.features.product_detail.presentation.ProductDetailScreen

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
                        popUpTo(0) { inclusive = true } // Clear auth history completely
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
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = { user ->
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
                ?: return@composable
            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() }
            )
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
            HomeScreen(
                onSearchClick = {
                    navController.navigate(NavItem.Search.route)
                },
                onNotificationClick = {
                },
                onCategoryClick = { category ->

                },
                onViewAllCategoriesClick = {
                },
                onBrandClick = { brand ->
                },
                onProductClick = { product ->
                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                }
            )
        }

        composable(NavItem.Search.route) {
            // Temporary presentation holders until ViewModel injection is configured
            var temporaryQueryString by remember { mutableStateOf("") }

            val sampleRecentSearches = remember {
                listOf(
                    ChipState(id = "1", name = "Silk Kaftans"),
                    ChipState(id = "2", name = "Oud Perfume")
                )
            }

            val sampleTrendingSearches = remember {
                listOf("Artisan Silver", "Woven Throws", "Hand-carved Oud")
            }

            SearchScreen(
                searchQuery = temporaryQueryString,
                onSearchQueryChange = { temporaryQueryString = it },
                recentSearches = sampleRecentSearches,
                trendingSearches = sampleTrendingSearches,
                searchResults = emptyList(), // Your teammate hooks up their live domain data stream here!
                onProductClick = { product ->
                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                },
                onFavoriteClick = { product -> /* Toggle data storage layer status */ },
                onRemoveRecentSearch = { id -> },
                onClearAllRecentSearches = { },
                onBackClick = { navController.popBackStack() },
                onFilterClick = { }
            )
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
                },
                onProductClick = {},
                onFavoriteClick = {}
            )
        }

        composable(NavItem.Profile.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Profile")
            }
        }
    }
}