package com.example.qafilah

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.qafilah.core.navigation.AppNavHost
import com.example.qafilah.core.navigation.NavItem
import com.example.qafilah.core.navigation.Screen
import com.example.qafilah.core.preferences.ThemeMode
import com.example.qafilah.core.util.LocalRealActivity
import com.example.qafilah.core.util.LocaleHelper
import com.example.qafilah.features.cart.domain.usecase.ObserveCartStateUseCase
import com.example.ui_kit.components.bottomnav.BottomNavBar
import com.example.ui_kit.components.bottomnav.BottomNavBarItem
import com.example.ui_kit.theme.QafilahTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = koinViewModel()
            val appState by mainViewModel.appState.collectAsState()

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) {}

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            val realActivityContext = LocalContext.current
            val localizedContext =
                LocaleHelper.wrapContext(realActivityContext, appState.languageCode)

            val layoutDirection = if (appState.languageCode == "ar") {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(
                LocalRealActivity provides this@MainActivity,
                LocalContext provides localizedContext,
                LocalActivityResultRegistryOwner provides this@MainActivity,
                LocalLayoutDirection provides layoutDirection
            ) {
                val darkTheme = when (appState.themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }

                QafilahTheme(darkTheme = darkTheme) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backStackEntry?.destination?.route

                    val observeCartStateUseCase: ObserveCartStateUseCase = koinInject()
                    val cart by observeCartStateUseCase().collectAsState(initial = null)
                    val cartItemCount = cart?.totalQuantity ?: 0

                    val bottomNavItems = NavItem.all.map { navItem ->
                        BottomNavBarItem(
                            route = navItem.route,
                            label = stringResource(id = navItem.label),
                            icon = navItem.icon,
                            badgeCount = if (navItem == NavItem.Cart) cartItemCount else 0
                        )
                    }

                    val isRouteHidden = currentRoute in Screen.hiddenRoutes

                    val showBottomBar = if (currentRoute == null) {
                        appState.initialDestination == InitialDestination.Home
                    } else {
                        !isRouteHidden
                    }

                    Scaffold(
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            AnimatedVisibility(
                                visible = showBottomBar,
                                enter = slideInVertically(initialOffsetY = { it }),
                                exit = slideOutVertically(targetOffsetY = { it })
                            ) {
                                BottomNavBar(
                                    items = bottomNavItems,
                                    currentRoute = currentRoute,
                                    onItemClick = { item ->
                                        navController.navigate(item.route) {
                                            popUpTo(NavItem.Home.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        val startRoute = when (appState.initialDestination) {
                            InitialDestination.Onboarding -> Screen.Onboarding.route
                            InitialDestination.Login -> Screen.Login.route
                            InitialDestination.Home -> NavItem.Home.route
                            InitialDestination.Splash -> Screen.Splash.route
                        }

                        AppNavHost(
                            navController = navController,
                            startDestination = startRoute,
                            modifier = Modifier.padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = 0.dp
                            )
                        )
                    }
                }
            }
        }
    }
}