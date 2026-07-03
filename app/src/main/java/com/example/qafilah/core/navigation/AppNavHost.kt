package com.example.qafilah.core.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.qafilah.features.home.presentation.ui.HomeScreen
import com.example.qafilah.features.catalog.presentation.CatalogProductsScreen
import com.example.qafilah.features.catalog.presentation.CatalogScreen
import com.example.qafilah.features.onboarding.OnboardingScreen
import com.example.qafilah.features.product_detail.presentation.ProductDetailScreen
import com.example.qafilah.features.profile.presentation.ProfileScreen
import com.example.qafilah.features.profile.presentation.editprofile.EditProfileScreen
import com.example.qafilah.features.profile.presentation.persondetails.PersonalDetailsScreen
import com.example.qafilah.features.profile.presentation.profile.ProfileViewModel
import com.example.qafilah.features.search.SearchScreen
import com.example.qafilah.features.search.domain.model.ChipState
import com.example.qafilah.features.search.presentation.SearchViewModel
import com.example.qafilah.features.splash.SplashScreen
import com.example.qafilah.features.wishlist.presentation.WishlistScreen
import org.koin.androidx.compose.koinViewModel

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
                        popUpTo(0) { inclusive = true }
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
                onSearchClick = { navController.navigate(NavItem.Search.route) },
                onNotificationClick = { },
                onBrandClick = { },
                onCategoryClick = { categoryUiModel ->
                    navController.navigate(
                        Screen.CatalogProducts.createRoute(categoryUiModel.id, categoryUiModel.label)
                    )
                },
                onViewAllCategoriesClick = {
                    navController.navigate(Screen.Catalog.route)
                },
                onProductClick = { product ->
                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                }
            )
        }
        composable(Screen.Catalog.route) {
            CatalogScreen(
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { categoryId, categoryTitle ->
                    navController.navigate(
                        Screen.CatalogProducts.createRoute(categoryId, categoryTitle)
                    )
                }
            )
        }
        composable(
            route = Screen.CatalogProducts.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("categoryTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryTitle = backStackEntry.arguments?.getString("categoryTitle") ?: ""

            CatalogProductsScreen(
                categoryId = categoryId,
                categoryTitle = categoryTitle,
                onBackClick = { navController.popBackStack() },
                onProductClick = { product ->
                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                }
            )
        }

        composable(NavItem.Search.route) {
            val searchViewModel: SearchViewModel = koinViewModel()
            val uiState by searchViewModel.uiState.collectAsState()

            SearchScreen(
                searchQuery = uiState.searchQuery,
                isLoading = uiState.isLoading,
                onSearchQueryChange = { searchViewModel.onSearchQueryChanged(it) },
                recentSearches = uiState.recentSearches,
                trendingSearches = uiState.trendingSearches,
                searchResults = uiState.searchResults,
                availableCategories = uiState.availableCategories,
                availableBrands = uiState.availableBrands,
                onCategoryFilterSelect = { searchViewModel.toggleCategoryFilter(it) },
                onBrandFilterSelect = { searchViewModel.toggleBrandFilter(it) },
                onResetFilters = { searchViewModel.resetFilters() },
                onProductClick = { product ->
                    searchViewModel.commitSearchQuery(uiState.searchQuery)
                    navController.navigate(
                        Screen.ProductDetail.createRoute(Uri.encode(product.id))
                    )
                },
                onFavoriteClick = { product ->
                    searchViewModel.toggleFavorite(product.id)
                },
                onRemoveRecentSearch = { query ->
                    searchViewModel.removeRecentSearch(query)
                },
                onClearAllRecentSearches = {
                    searchViewModel.clearAllRecentSearches()
                },
                onBackClick = {
                    navController.popBackStack()
                }
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
                onProductClick = { product ->
                    navController.navigate(Screen.ProductDetail.createRoute(Uri.encode(product.id)))
                },
                onFavoriteClick = { }
            )
        }

        composable(NavItem.Profile.route) {
            val profileViewModel: ProfileViewModel = koinViewModel()
            ProfileScreen(
                viewModel = profileViewModel,
                onEditProfileClick = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onPersonalDetailsClick = {
                    navController.navigate(Screen.PersonalDetails.route)
                },
                onSavedPaymentsClick = {
                },
                onShippingAddressesClick = {
                    navController.navigate(Screen.AddAddress.route)
                },
                onSignOutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PersonalDetails.route) {
            PersonalDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate(Screen.EditProfile.route) }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCancelClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AddAddress.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Add Address (Work in Progress)")
            }
        }

        composable(
            route = Screen.EditAddress.route,
            arguments = listOf(
                navArgument("addressId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId") ?: return@composable
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Edit Address: $addressId (Work in Progress)")
            }
        }

    }
}