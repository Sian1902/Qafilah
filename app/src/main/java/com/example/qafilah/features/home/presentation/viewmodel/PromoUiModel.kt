package com.example.qafilah.features.home.presentation.viewmodel

import androidx.annotation.StringRes

data class PromoUiModel(
    val id: String,
    val imageUrl: String,
    @StringRes val titleRes: Int,
    @StringRes val ctaTextRes: Int,
    val code: String
)