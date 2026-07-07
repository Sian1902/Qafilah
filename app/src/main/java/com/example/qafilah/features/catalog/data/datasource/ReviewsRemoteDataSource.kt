package com.example.qafilah.features.catalog.data.datasource

interface ReviewsRemoteDataSource{
    suspend fun createReviewMetaobject(
        productId: String,
        name: String,
        rating: Int,
        title: String,
        body: String,
        date: String
    ): String?

    suspend fun getProductReviewMetafieldValue(productId: String): String?

    suspend fun setProductReviewMetafield(productId: String, jsonArrayString: String)
}