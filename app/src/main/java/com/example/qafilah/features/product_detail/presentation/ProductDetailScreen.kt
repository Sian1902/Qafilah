package com.example.qafilah.features.product_detail.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qafilah.R
import com.example.ui_kit.components.product.ExpandableDescriptionBlock
import com.example.ui_kit.components.product.ProductImageHeader
import com.example.ui_kit.components.product.RatingBadge
import com.example.ui_kit.components.product.SelectablePillGroup
import com.example.ui_kit.components.product.SpecItem
import com.example.ui_kit.components.product.SpecTagPairs
import com.example.ui_kit.components.product.StickyBottomBar
import com.example.ui_kit.components.product.TopIconBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val defaultCollectionLabel = stringResource(R.string.default_collection_label)
    val inStockTemplate = stringResource(R.string.stock_in_stock)
    val outOfStockText = stringResource(R.string.stock_out_of_stock)
    val sharedProductTemplate = stringResource(R.string.share_product_message)
    val loadProductErrorFallback = stringResource(R.string.error_failed_to_load_product_details)
    val addToCartErrorFallback = stringResource(R.string.error_failed_to_add_to_cart)

    val productDescriptionLabel = stringResource(R.string.product_description)
    val readMoreLabel = stringResource(R.string.read_more)
    val readLessLabel = stringResource(R.string.read_less)
    val backDesc = stringResource(R.string.back)
    val shareDesc = stringResource(R.string.share)
    val addToWishlistDesc = stringResource(R.string.add_to_wishlist)
    val removeFromWishlistDesc = stringResource(R.string.remove_from_wishlist)

    val wishlistErrorFallback = stringResource(R.string.error_something_went_wrong)
    val wishlistAddedTemplate = stringResource(R.string.wishlist_item_added)
    val wishlistNotLoggedInMessage = stringResource(R.string.error_login_to_manage_wishlist)
    val cartNotLoggedInMessage = stringResource(R.string.error_login_to_add_to_cart)
    val addToCartSuccessMessage = stringResource(R.string.added_to_cart_success)
    val decodedId = android.net.Uri.decode(productId)

    LaunchedEffect(decodedId) {
        viewModel.loadProduct(decodedId, loadProductErrorFallback)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProductDetailEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is ProductDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is ProductDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            is ProductDetailUiState.Success -> {
                val product = state.product
                val selectedVariant = state.selectedVariant
                val selectedOptions = state.selectedOptions
                val isFavorite = state.isFavorite
                val isAddingToCart = state.isAddingToCart
                val addToCartError = state.addToCartError

                LaunchedEffect(addToCartError) {
                    if (addToCartError != null) {
                        Toast.makeText(context, addToCartError, Toast.LENGTH_LONG).show()
                        viewModel.dismissCartError()
                    }
                }

                val optionGroups = remember(product.variants) {
                    val groups = mutableMapOf<String, MutableList<String>>()
                    for (variant in product.variants) {
                        for ((name, value) in variant.options) {
                            if (name.equals("Title", ignoreCase = true) && value.equals(
                                    "Default Title",
                                    ignoreCase = true
                                )
                            ) {
                                continue
                            }
                            val list = groups.getOrPut(name) { mutableListOf() }
                            if (!list.contains(value)) {
                                list.add(value)
                            }
                        }
                    }
                    groups.toMap()
                }

                val specs = remember { emptyList<SpecItem>() }

                val inStock =
                    selectedVariant.inventoryQuantity != null && selectedVariant.inventoryQuantity > 0
                val stockText = if (inStock) {
                    String.format(inStockTemplate, selectedVariant.inventoryQuantity)
                } else {
                    outOfStockText
                }
                val stockColor =
                    if (inStock) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

                val rating = product.rating
                val reviewCount = product.ratingCount

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ProductImageHeader(
                                images = product.images,
                                collectionName = product.tags.firstOrNull()?.uppercase()
                                    ?: defaultCollectionLabel,
                                imageContentDescription = { page ->
                                    stringResource(
                                        R.string.product_image_cd,
                                        page + 1,
                                        product.title
                                    )
                                }
                            )
                            TopIconBar(
                                isFavorite = isFavorite,
                                onBackClick = onBackClick,
                                onShareClick = {
                                    Toast.makeText(
                                        context,
                                        String.format(sharedProductTemplate, product.title),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onFavoriteToggle = {
                                    viewModel.toggleFavorite(
                                        notLoggedInMessage = wishlistNotLoggedInMessage,
                                        fallbackErrorMessage = wishlistErrorFallback,
                                        addedToWishlistTemplate = wishlistAddedTemplate
                                    )
                                },
                                backContentDescription = backDesc,
                                shareContentDescription = shareDesc,
                                addToWishlistContentDescription = addToWishlistDesc,
                                removeFromWishlistContentDescription = removeFromWishlistDesc
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(stockColor)
                                )
                                Text(
                                    text = stockText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp,
                                        color = stockColor
                                    )
                                )
                            }

                            Text(
                                text = product.title,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 28.sp,
                                    lineHeight = 36.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            RatingBadge(
                                rating = rating,
                                reviewCount = reviewCount,
                                starContentDescription = stringResource(R.string.product_rating_star_cd),
                                ratingLabel = stringResource(
                                    R.string.product_rating_label,
                                    rating ?: 0.0,
                                    reviewCount ?: 0
                                )
                            )

                            if (optionGroups.isNotEmpty()) {
                                optionGroups.forEach { (optionName, optionValues) ->
                                    SelectablePillGroup(
                                        selectionLabel = optionName,
                                        options = optionValues,
                                        selectedOption = selectedOptions[optionName],
                                        onOptionSelected = { selectedVal ->
                                            viewModel.selectOption(optionName, selectedVal)
                                        }
                                    )
                                }
                            }

                            ExpandableDescriptionBlock(
                                title = productDescriptionLabel,
                                descriptionHtml = product.description ?: "",
                                readMoreLabel = readMoreLabel,
                                readLessLabel = readLessLabel
                            )

                            SpecTagPairs(specs = specs)

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    StickyBottomBar(
                        price = state.displayPrice,
                        totalPriceLabel = stringResource(R.string.product_total_price_label),
                        addToCartLabel = stringResource(R.string.product_add_to_cart_label),
                        addToCartContentDescription = stringResource(R.string.product_add_to_cart_cd),
                        isLoading = isAddingToCart,
                        onAddToCartClick = {
                            viewModel.addToCart(
                                variantId = selectedVariant.id,
                                fallbackErrorMessage = addToCartErrorFallback,
                                notLoggedInMessage = cartNotLoggedInMessage,
                                successMessage = addToCartSuccessMessage
                            )
                        }
                    )
                }
            }
        }
    }
}
