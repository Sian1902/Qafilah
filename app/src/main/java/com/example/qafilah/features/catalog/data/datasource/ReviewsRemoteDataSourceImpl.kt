package com.example.qafilah.features.catalog.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.graphql.admin.CreateReviewMetaobjectMutation
import com.example.qafilah.graphql.admin.GetProductReviewsMetafieldQuery
import com.example.qafilah.graphql.admin.SetProductReviewsMetafieldMutation
import com.example.qafilah.graphql.admin.type.MetaobjectCapabilityDataInput
import com.example.qafilah.graphql.admin.type.MetaobjectCapabilityDataPublishableInput
import com.example.qafilah.graphql.admin.type.MetaobjectCreateInput
import com.example.qafilah.graphql.admin.type.MetaobjectFieldInput
import com.example.qafilah.graphql.admin.type.MetaobjectStatus

class ReviewsRemoteDataSourceImpl(
    private val adminApolloClient: ApolloClient
): ReviewsRemoteDataSource {
    override suspend fun createReviewMetaobject(
        productId: String,
        name: String,
        rating: Int,
        title: String,
        body: String,
        date: String
    ): String? {
        val fields = listOf(
            MetaobjectFieldInput("product", productId),
            MetaobjectFieldInput("customer_name", name),
            MetaobjectFieldInput("rating", rating.toString()),
            MetaobjectFieldInput("title", title),
            MetaobjectFieldInput("body", body),
            MetaobjectFieldInput("created_at", date),
            MetaobjectFieldInput("approved", "true")
        )

        val capabilities = MetaobjectCapabilityDataInput(
            publishable = Optional.present(
                MetaobjectCapabilityDataPublishableInput(
                    status = MetaobjectStatus.ACTIVE
                )
            )
        )

        val input = MetaobjectCreateInput(
            type = "marketak_product_review",
            fields = Optional.present(fields),
            capabilities = Optional.present(capabilities)
        )

        val response = safeApiCall {
            adminApolloClient.mutation(CreateReviewMetaobjectMutation(input)).execute()
        }

        val errors = response.metaobjectCreate?.userErrors
        if (!errors.isNullOrEmpty()) throw Exception(errors.first().message)

        return response.metaobjectCreate?.metaobject?.id
    }

    override suspend fun getProductReviewMetafieldValue(productId: String): String? {
        val response = safeApiCall {
            adminApolloClient.query(GetProductReviewsMetafieldQuery(productId)).execute()
        }
        return response.product?.metafield?.value
    }

    override suspend fun setProductReviewMetafield(productId: String, jsonArrayString: String) {
        val response = safeApiCall {
            adminApolloClient.mutation(
                SetProductReviewsMetafieldMutation(
                    productId,
                    jsonArrayString
                )
            ).execute()
        }

        val errors = response.metafieldsSet?.userErrors
        if (!errors.isNullOrEmpty()) throw Exception(errors.first().message)
    }
}