package com.example.qafilah.features.home.presentation.ui

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.home.presentation.viewmodel.PromoUiModel
import com.example.ui_kit.components.shared.PromoBannerCard
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PromosPager(
    promos: List<PromoUiModel>,
    onClaimPromo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (promos.isEmpty()) return

    val actualCount = promos.size
    val virtualCount = Int.MAX_VALUE
    val startIndex = remember(actualCount) {
        virtualCount / 2 - (virtualCount / 2 % actualCount)
    }

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { virtualCount }
    )

    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(pagerState, isDragged, actualCount) {
        if (actualCount > 1 && !isDragged) {
            while (true) {
                delay(2000.milliseconds)
                val next = pagerState.currentPage + 1
                pagerState.animateScrollToPage(next)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 20.dp),
        pageSpacing = 16.dp,
        modifier = modifier.fillMaxWidth()
    ) { page ->
        val promo = promos[page % actualCount]

        PromoBannerCard(
            imageUrl = promo.imageUrl,
            title = promo.title,
            tagLabel = stringResource(R.string.promo_tag_label),
            imageContentDescription = promo.title,
            ctaText = promo.ctaText,
            onCtaClick = { onClaimPromo(promo.code) }
        )
    }
}
