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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui_kit.components.product.ExpandableDescriptionBlock
import com.example.ui_kit.components.product.ProductImageHeader
import com.example.ui_kit.components.product.RatingBadge
import com.example.ui_kit.components.product.SelectablePillGroup
import com.example.ui_kit.components.product.SpecItem
import com.example.ui_kit.components.product.SpecTagPairs
import com.example.ui_kit.components.product.StickyBottomBar
import com.example.ui_kit.components.product.TopIconBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val decodedId = android.net.Uri.decode(productId)
    LaunchedEffect(decodedId) {
        viewModel.loadProduct(decodedId)
    }

    when (val state = uiState) {
        is ProductDetailUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is ProductDetailUiState.Error -> {
            Box(
                modifier = modifier
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

            val optionGroups = remember(product.variants) {
                val groups = mutableMapOf<String, MutableList<String>>()
                for (variant in product.variants) {
                    for (opt in variant.selectedOptions) {
                        if (opt.name.equals(
                                "Title",
                                ignoreCase = true
                            ) && opt.value.equals("Default Title", ignoreCase = true)
                        ) {
                            continue
                        }
                        val list = groups.getOrPut(opt.name) { mutableListOf() }
                        if (!list.contains(opt.value)) {
                            list.add(opt.value)
                        }
                    }
                }
                groups.toMap()
            }

            val specKeys = listOf(
                "custom.flex" to "FLEX",
                "custom.flex_rating" to "FLEX",
                "custom.terrain" to "TERRAIN",
                "custom.material" to "MATERIAL",
                "custom.profile" to "PROFILE"
            )
            val specs = remember(product.metafields) {
                specKeys.mapNotNull { (key, label) ->
                    val value = product.metafields[key]
                    if (!value.isNullOrBlank()) SpecItem(label, value) else null
                }
            }

            val inStock =
                selectedVariant.inventoryQuantity != null && selectedVariant.inventoryQuantity > 0
            val stockText = if (inStock) {
                "In Stock (${selectedVariant.inventoryQuantity} available)"
            } else {
                "Out of Stock"
            }
            val stockColor =
                if (inStock) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

            val rating = product.getMetafield("reviews.rating")?.toDoubleOrNull()
            val reviewCount = product.getMetafield("reviews.rating_count")?.toIntOrNull()

            Column(
                modifier = modifier
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
                            images = product.images.map { it.url },
                            collectionName = product.getMetafield("custom.collection")
                                ?: "THE COLLECTION"
                        )
                        TopIconBar(
                            isFavorite = isFavorite,
                            onBackClick = onBackClick,
                            onShareClick = {
                                Toast.makeText(
                                    context,
                                    "Shared product: ${product.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFavoriteToggle = { viewModel.toggleFavorite() }
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

                        RatingBadge(rating = rating, reviewCount = reviewCount)

                        if (optionGroups.isNotEmpty()) {
                            optionGroups.forEach { (optionName, optionValues) ->
                                SelectablePillGroup(
                                    title = optionName,
                                    options = optionValues,
                                    selectedOption = selectedOptions[optionName],
                                    onOptionSelected = { selectedVal ->
                                        viewModel.selectOption(optionName, selectedVal)
                                    }
                                )
                            }
                        }

                        ExpandableDescriptionBlock(descriptionHtml = product.descriptionHtml)

                        SpecTagPairs(specs = specs)

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                StickyBottomBar(
                    price = "$${selectedVariant.price}",
                    onAddToCartClick = {
                        Toast.makeText(
                            context,
                            "Added variant: ${selectedVariant.title} to cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}
