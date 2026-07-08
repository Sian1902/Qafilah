package com.example.qafilah.features.catalog.domain.model

data class SubmitReviewParams(
    val productId: String,
    val customerName: String,
    val rating: Int,
    val title: String,
    val body: String,
    val dateString: String
)