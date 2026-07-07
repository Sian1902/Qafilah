package com.example.qafilah.features.product_detail.presentation.ui

import android.net.Uri
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qafilah.R
import com.example.qafilah.features.product_detail.presentation.viewmodel.ProductDetailEvent
import com.example.qafilah.features.product_detail.presentation.viewmodel.ProductDetailLabels
import com.example.qafilah.features.product_detail.presentation.viewmodel.ProductDetailUiState
import com.example.qafilah.features.product_detail.presentation.viewmodel.ProductDetailViewModel
import com.example.ui_kit.components.product.*
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = rememberProductDetailStrings()
    val decodedId = remember(productId) { Uri.decode(productId) }

    LaunchedEffect(decodedId) {
        val labels = ProductDetailLabels(
            defaultCollectionLabel = strings.defaultCollectionLabel,
            inStockTemplate = strings.inStockTemplate,
            outOfStockText = strings.outOfStockText,
            ratingLabelTemplate = "%f (%d)"
        )
        viewModel.loadProduct(decodedId, strings.loadProductErrorFallback, labels)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is ProductDetailEvent.ShowToast) {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(modifier = modifier) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is ProductDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                is ProductDetailUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is ProductDetailUiState.Success -> {
                    ProductDetailContent(
                        state = state,
                        strings = strings,
                        onBackClick = onBackClick,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    state: ProductDetailUiState.Success,
    strings: ProductDetailStrings,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel
) {
    val context = LocalContext.current
    val product = state.product
    val specs = remember { emptyList<SpecItem>() }

    val (reviewTitle, setReviewTitle) = remember { mutableStateOf("") }
    val (reviewBody, setReviewBody) = remember { mutableStateOf("") }
    val (reviewRating, setReviewRating) = remember { mutableStateOf(0) }

    val isSubmittingReview by viewModel.isSubmittingReview.collectAsStateWithLifecycle()

    LaunchedEffect(state.addToCartError) {
        if (state.addToCartError != null) {
            Toast.makeText(context, state.addToCartError, Toast.LENGTH_LONG).show()
            viewModel.dismissCartError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is ProductDetailEvent.ReviewSubmittedSuccessfully) {
                setReviewTitle("")
                setReviewBody("")
                setReviewRating(0)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ProductImageHeader(
                    images = product.images,
                    collectionName = product.tag,
                    imageContentDescription = { page ->
                        stringResource(R.string.product_image_cd, page + 1, product.title)
                    }
                )
                TopIconBar(
                    isFavorite = product.isFavorite,
                    onBackClick = onBackClick,
                    onShareClick = {
                        Toast.makeText(
                            context,
                            String.format(strings.sharedProductTemplate, product.title),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onFavoriteToggle = {
                        viewModel.toggleFavorite(
                            notLoggedInMessage = strings.wishlistNotLoggedInMessage,
                            fallbackErrorMessage = strings.wishlistErrorFallback,
                            addedToWishlistTemplate = strings.wishlistAddedTemplate
                        )
                    },
                    backContentDescription = strings.backDesc,
                    shareContentDescription = strings.shareDesc,
                    addToWishlistContentDescription = strings.addToWishlistDesc,
                    removeFromWishlistContentDescription = strings.removeFromWishlistDesc
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
                            .background(Color(product.stockColorInt))
                    )
                    Text(
                        text = product.stockText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = Color(product.stockColorInt)
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
                    rating = product.rating,
                    reviewCount = product.reviewCount,
                    starContentDescription = stringResource(R.string.product_rating_star_cd),
                    ratingLabel = product.ratingLabel
                )

                if (product.optionGroups.isNotEmpty()) {
                    product.optionGroups.forEach { (optionName, optionValues) ->
                        SelectablePillGroup(
                            selectionLabel = optionName,
                            options = optionValues,
                            selectedOption = product.selectedOptions[optionName],
                            onOptionSelected = { selectedVal ->
                                viewModel.selectOption(optionName, selectedVal)
                            }
                        )
                    }
                }

                ExpandableDescriptionBlock(
                    title = strings.productDescriptionLabel,
                    descriptionHtml = product.description,
                    readMoreLabel = strings.readMoreLabel,
                    readLessLabel = strings.readLessLabel
                )

                SpecTagPairs(specs = specs)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = strings.reviewsSectionHeader,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (product.reviews.isNotEmpty()) {
                        RatingHistogram(
                            averageRating = product.rating ?: 0.0,
                            totalReviews = product.reviewCount ?: 0,
                            ratingLabel = "${product.reviewCount} Reviews",
                            breakdown = product.ratingBreakdown.map { Pair(it.stars, it.percentage) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    AddReviewCard(
                        title = reviewTitle,
                        onTitleChange = setReviewTitle,
                        body = reviewBody,
                        onBodyChange = setReviewBody,
                        rating = reviewRating,
                        onRatingChange = setReviewRating,
                        isSubmitting = isSubmittingReview,
                        onSubmitClick = {
                            viewModel.submitReview(
                                title = reviewTitle,
                                body = reviewBody,
                                rating = reviewRating,
                                successMessage = strings.submitReviewSuccessMessage,
                                errorMessage = strings.submitReviewErrorMessage
                            )
                        },
                        cardTitle = strings.addReviewCardTitle,
                        nameLabel = strings.addReviewNameLabel,
                        titleLabel = strings.addReviewTitleLabel,
                        bodyLabel = strings.addReviewBodyLabel,
                        submitButtonText = strings.addReviewSubmitText
                    )

                    if (product.reviews.isEmpty()) {
                        Text(
                            text = strings.emptyReviewsPlaceholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        product.reviews.forEach { review ->
                            ReviewCard(
                                customerName = review.customerName,
                                rating = review.rating,
                                title = review.title,
                                body = review.body,
                                createdAt = review.createdAt,
                                starContentDescription = stringResource(R.string.product_rating_star_cd),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        StickyBottomBar(
            price = product.displayPrice,
            totalPriceLabel = stringResource(R.string.product_total_price_label),
            addToCartLabel = if (product.isInStock) stringResource(R.string.product_add_to_cart_label) else strings.outOfStockText,
            addToCartContentDescription = stringResource(R.string.product_add_to_cart_cd),
            isLoading = state.isAddingToCart,
            onAddToCartClick = {
                if (product.isInStock) {
                    viewModel.addToCart(
                        variantId = product.selectedVariantId,
                        fallbackErrorMessage = strings.addToCartErrorFallback,
                        notLoggedInMessage = strings.cartNotLoggedInMessage,
                        quantity = 1,
                        successMessage = strings.addToCartSuccessMessage
                    )
                } else {
                    Toast.makeText(context, strings.outOfStockText, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}


