package com.example.ui_kit.components.shimmer


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun PromosPagerSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            // 180.dp is a standard height for a prominent promo banner;
            // adjust this if your PromoBannerCard is taller/shorter.
            .height(180.dp)
            // Matches the horizontal padding of your actual HorizontalPager
            .padding(horizontal = 20.dp)
            // Standard rounded corners for banners
            .clip(RoundedCornerShape(16.dp))
            // Apply our reusable modifier
            .shimmerEffect()
    )
}