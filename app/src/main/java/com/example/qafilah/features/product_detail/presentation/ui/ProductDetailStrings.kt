package com.example.qafilah.features.product_detail.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.qafilah.R

@Composable
fun rememberProductDetailStrings() = ProductDetailStrings(
    defaultCollectionLabel = stringResource(R.string.default_collection_label),
    inStockTemplate = stringResource(R.string.stock_in_stock),
    outOfStockText = stringResource(R.string.stock_out_of_stock),
    sharedProductTemplate = stringResource(R.string.share_product_message),
    loadProductErrorFallback = stringResource(R.string.error_failed_to_load_product_details),
    addToCartErrorFallback = stringResource(R.string.error_failed_to_add_to_cart),
    productDescriptionLabel = stringResource(R.string.product_description),
    readMoreLabel = stringResource(R.string.read_more),
    readLessLabel = stringResource(R.string.read_less),
    backDesc = stringResource(R.string.back),
    shareDesc = stringResource(R.string.share),
    addToWishlistDesc = stringResource(R.string.add_to_wishlist),
    removeFromWishlistDesc = stringResource(R.string.remove_from_wishlist),
    wishlistErrorFallback = stringResource(R.string.error_something_went_wrong),
    wishlistAddedTemplate = stringResource(R.string.wishlist_item_added),
    wishlistNotLoggedInMessage = stringResource(R.string.error_login_to_manage_wishlist),
    cartNotLoggedInMessage = stringResource(R.string.error_login_to_add_to_cart),
    addToCartSuccessMessage = stringResource(R.string.added_to_cart_success),
    reviewsSectionHeader = "Customer Reviews",
    emptyReviewsPlaceholder = "No reviews yet."
)

data class ProductDetailStrings(
    val defaultCollectionLabel: String,
    val inStockTemplate: String,
    val outOfStockText: String,
    val sharedProductTemplate: String,
    val loadProductErrorFallback: String,
    val addToCartErrorFallback: String,
    val productDescriptionLabel: String,
    val readMoreLabel: String,
    val readLessLabel: String,
    val backDesc: String,
    val shareDesc: String,
    val addToWishlistDesc: String,
    val removeFromWishlistDesc: String,
    val wishlistErrorFallback: String,
    val wishlistAddedTemplate: String,
    val wishlistNotLoggedInMessage: String,
    val cartNotLoggedInMessage: String,
    val addToCartSuccessMessage: String,
    val reviewsSectionHeader: String,
    val emptyReviewsPlaceholder: String
)