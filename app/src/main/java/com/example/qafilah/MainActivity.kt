package com.example.qafilah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.qafilah.navigation.AppNavHost
import com.example.qafilah.navigation.NavItem
import com.example.qafilah.navigation.Screen
import com.example.ui_kit.components.bottomnav.BottomNavBar
import com.example.ui_kit.components.bottomnav.BottomNavBarItem
import com.example.ui_kit.theme.QafilahTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QafilahTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                val bottomNavItems = NavItem.all.map { navItem ->
                    BottomNavBarItem(
                        route = navItem.route,
                        label = navItem.label,
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
                                        // Avoid building up a large back stack
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
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}