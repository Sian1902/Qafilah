package com.example.qafilah.core.navigation


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Chat : Screen("chat")
    object ProductDetail : Screen("product_detail?productId={productId}") {
        fun createRoute(productId: String) =
            "product_detail?productId=${android.net.Uri.encode(productId)}"
    }

    object Checkout : Screen("checkout")
    object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun createRoute(orderId: String) = "order_confirmation/${android.net.Uri.encode(orderId)}"
    }
    object OrderDetails : Screen("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/${android.net.Uri.encode(orderId)}"
    }
    object Catalog : Screen("catalog_screen")

    object CatalogProducts : Screen("catalog_products/{categoryId}/{categoryTitle}") {
        fun createRoute(categoryId: String, categoryTitle: String): String {
            val encodedId = android.net.Uri.encode(categoryId)
            val encodedTitle = android.net.Uri.encode(categoryTitle)
            return "catalog_products/$encodedId/$encodedTitle"
        }
    }

    object PersonalDetails : Screen("personal_details")
    object EditProfile : Screen("edit_profile")
    object ShippingAddresses : Screen("shipping_addresses")
    object Orders : Screen("orders")
    object AddAddress : Screen("add_address")
    object EditAddress : Screen("edit_address/{addressId}") {
        fun createRoute(addressId: String) = "edit_address/$addressId"
    }

    object CamelRun : Screen("camel_run")

    companion object {
        val hiddenRoutes = setOf(
            Splash.route,
            Onboarding.route,
            Login.route,
            Register.route,
            Checkout.route,
            "product_detail?productId={productId}",
            "order_confirmation/{orderId}",
            PersonalDetails.route,
            EditProfile.route,
            ShippingAddresses.route,
            Orders.route,
            AddAddress.route,
            "edit_address/{addressId}",
            Chat.route,
            CamelRun.route
        )
    }
}