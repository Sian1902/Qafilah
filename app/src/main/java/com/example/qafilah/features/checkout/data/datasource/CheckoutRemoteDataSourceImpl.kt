package com.example.qafilah.features.checkout.data.datasource

import com.apollographql.apollo.ApolloClient
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository

class CheckoutRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
): CheckoutRemoteDataSource {
}