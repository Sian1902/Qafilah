package com.example.qafilah.core.navigation


import androidx.annotation.DrawableRes
import com.example.qafilah.R

sealed class NavItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
) {
    object Home : NavItem(
        route = "home",
        label = "Home",
        icon = R.drawable.home
    )

    object Search : NavItem(
        route = "search",
        label = "Search",
        icon = R.drawable.search
    )

    object Cart : NavItem(
        route = "cart",
        label = "Cart",
        icon = R.drawable.cart
    )

    object Wishlist : NavItem(
        route = "wishlist",
        label = "Wishlist",
        icon = R.drawable.cart
    )

    object Profile : NavItem(
        route = "profile",
        label = "Profile",
        icon = R.drawable.profile
    )

    companion object {
        val all = listOf(Home, Search, Cart, Wishlist, Profile)
    }
}
