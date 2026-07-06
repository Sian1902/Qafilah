package com.example.qafilah.core.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.qafilah.MainViewModel
import com.example.qafilah.R
import com.example.qafilah.features.address.presentation.AddressViewModel
import com.example.qafilah.features.address.presentation.ShippingAddressesScreen
import com.example.qafilah.features.assistant.presentation.ChatScreen
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.presentation.screens.LoginScreen
import com.example.qafilah.features.auth.presentation.screens.SignUpScreen
import com.example.qafilah.features.cart.presentation.ui.CartScreen
import com.example.qafilah.features.catalog.presentation.CatalogProductsScreen
import com.example.qafilah.features.catalog.presentation.CatalogScreen
import com.example.qafilah.features.checkout.presentation.shared.CheckoutScreen
import com.example.qafilah.features.home.presentation.ui.HomeScreen
import com.example.qafilah.features.onboarding.OnboardingScreen
import com.example.qafilah.features.product_detail.presentation.ProductDetailScreen
import com.example.qafilah.features.profile.presentation.editprofile.EditProfileScreen
import com.example.qafilah.features.orders.presentation.order_details.OrderDetailsScreen
import com.example.qafilah.features.orders.presentation.order_details.OrderDetailsViewModel
import com.example.qafilah.features.orders.presentation.order.OrdersScreen
import com.example.qafilah.features.orders.presentation.order.OrdersViewModel
import com.example.qafilah.features.profile.presentation.persondetails.PersonalDetailsScreen
import com.example.qafilah.features.profile.presentation.profile.ProfileScreen
import com.example.qafilah.features.profile.presentation.profile.ProfileViewModel
import com.example.qafilah.features.search.SearchScreen
import com.example.qafilah.features.search.presentation.SearchViewModel
import com.example.qafilah.features.splash.SplashScreen
import com.example.qafilah.features.wishlist.presentation.WishlistScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(startDestination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    mainViewModel.setOnboardingCompleted()
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
                        popUpTo(0) { inclusive = true }
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
                CheckoutScreen(
                    onNavigateToAddress = {
                        navController.navigate(Screen.ShippingAddresses.route)
                    }
                )
            }
        }

        composable(
            route = Screen.OrderConfirmation.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
                ?: return@composable
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.order_confirmation, orderId))
            }
        }

        composable(NavItem.Home.route) {
            HomeScreen(
                onSearchClick = { navController.navigate(NavItem.Search.route) },
                onNotificationClick = { },
                onBrandClick = { categoryName ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "search_category",
                        categoryName.lowercase()
                    )
                    navController.navigate(NavItem.Search.route)
                },
                onCategoryClick = { categoryUiModel ->
                    navController.navigate(
                        Screen.CatalogProducts.createRoute(
                            categoryUiModel.id,
                            categoryUiModel.label
                        )
                    )
                },
                onViewAllCategoriesClick = {
                    navController.navigate(Screen.Catalog.route)
                },
                onProductClick = { product ->
                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                },
                onChatFabClick = { navController.navigate(Screen.Chat.route) }
            )
        }
        composable(Screen.Chat.route) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
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

            val initialCategory = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("search_category")
            val initialBrand = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("search_brand")

            LaunchedEffect(Unit) {
                if (initialCategory != null || initialBrand != null) {
                    searchViewModel.applyInitialFilters(initialCategory, initialBrand)
                    navController.previousBackStackEntry?.savedStateHandle?.remove<String>("search_category")
                    navController.previousBackStackEntry?.savedStateHandle?.remove<String>("search_brand")
                }
            }

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
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                },
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(Uri.encode(productId)))
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
                onOrdersClick = {
                    navController.navigate(Screen.Orders.route)
                },
                onSavedPaymentsClick = {
                },
                onShippingAddressesClick = {
                    navController.navigate(Screen.ShippingAddresses.route)
                },
                onSignOutClick = {
                    profileViewModel.signOut {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
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

        composable(Screen.Orders.route) {
            val ordersViewModel: OrdersViewModel = koinViewModel()
            val notAuthenticatedMessage = stringResource(R.string.error_not_authenticated)
            val unknownErrorMessage = stringResource(R.string.error_unknown)
            
            LaunchedEffect(Unit) {
                ordersViewModel.loadOrders(notAuthenticatedMessage, unknownErrorMessage)
            }
            OrdersScreen(
                viewModel = ordersViewModel,
                onBackClick = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderDetails.createRoute(orderId))
                }
            )
        }

        composable(
            route = Screen.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val detailsViewModel: OrderDetailsViewModel = koinViewModel()
            val orderNotFoundMessage = stringResource(R.string.error_order_not_found)
            
            OrderDetailsScreen(
                orderId = orderId,
                viewModel = detailsViewModel,
                orderNotFoundMessage = orderNotFoundMessage,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCancelClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ShippingAddresses.route) {
            val addressViewModel: AddressViewModel = koinViewModel()
            val uiState by addressViewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                addressViewModel.loadAddresses()
            }

            ShippingAddressesScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                onSaveNewAddress = { address ->
                    addressViewModel.createAddress(address)
                },
                onEditAddress = { address ->
                    addressViewModel.updateAddress(address)
                },
                onDeleteAddress = { addressId ->
                    addressViewModel.deleteAddress(addressId)
                },
                onSetDefaultAddress = { addressId ->
                    addressViewModel.setDefaultAddress(addressId)
                },
                onConsumeOperationResult = {
                    addressViewModel.consumeOperationResult()
                }
            )

        }

        composable(Screen.AddAddress.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.add_address_wip))
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
                Text(stringResource(R.string.edit_address_wip, addressId))
            }
        }

    }
}