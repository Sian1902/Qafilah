package com.example.qafilah.core.navigation


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.qafilah.R

sealed class NavItem(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    object Home : NavItem(
        route = "home",
        label = R.string.nav_home,
        icon = R.drawable.ic_home
    )

    object Search : NavItem(
        route = "search",
        label = R.string.nav_search,
        icon = R.drawable.ic_search
    )

    object Cart : NavItem(
        route = "cart",
        label = R.string.nav_cart,
        icon = R.drawable.ic_shopping_cart
    )

    object Wishlist : NavItem(
        route = "wishlist",
        label = R.string.nav_wishlist,
        icon = R.drawable.ic_favorite
    )

    object Profile : NavItem(
        route = "profile",
        label = R.string.nav_profile,
        icon = R.drawable.ic_person
    )

    companion object {
        val all = listOf(Home, Search, Cart, Wishlist, Profile)

        val guestRestricted = setOf(Cart.route, Wishlist.route)
    }
}