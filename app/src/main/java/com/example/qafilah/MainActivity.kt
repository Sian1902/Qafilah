package com.example.qafilah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.qafilah.core.navigation.AppNavHost
import com.example.qafilah.core.navigation.NavItem
import com.example.qafilah.core.navigation.Screen
import com.example.qafilah.core.preferences.ThemeMode
import com.example.qafilah.core.util.LocalRealActivity
import com.example.qafilah.core.util.LocaleHelper
import com.example.ui_kit.components.bottomnav.BottomNavBar
import com.example.ui_kit.components.bottomnav.BottomNavBarItem
import com.example.ui_kit.theme.QafilahTheme
import org.koin.androidx.compose.koinViewModel
import androidx.activity.compose.LocalActivityResultRegistryOwner

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = koinViewModel()
            val appState by mainViewModel.appState.collectAsState()

            val realActivityContext = LocalContext.current
            val localizedContext = LocaleHelper.wrapContext(realActivityContext, appState.languageCode)

                CompositionLocalProvider(
            LocalRealActivity provides this@MainActivity,
            LocalContext provides localizedContext,
            LocalActivityResultRegistryOwner provides this@MainActivity
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

                    val bottomNavItems = NavItem.all.map { navItem ->
                        BottomNavBarItem(
                            route = navItem.route,
                            label = stringResource(id = navItem.label),
                            icon = navItem.icon
                        )
                    }

                    val showBottomBar = currentRoute !in Screen.hiddenRoutes

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
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
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}