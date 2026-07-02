package com.example.qafilah.core.navigation


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object ProductDetail : Screen("product_detail?productId={productId}") {
        fun createRoute(productId: String) =
            "product_detail?productId=${android.net.Uri.encode(productId)}"
    }

    object Checkout : Screen("checkout")
    object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun createRoute(orderId: String) = "order_confirmation/$orderId"
    }

    companion object {
        val hiddenRoutes = setOf(
            Splash.route,
            Onboarding.route,
            Login.route,
            Register.route,
            Checkout.route,
            "product_detail?productId={productId}",
            "order_confirmation/{orderId}"
        )
    }
}