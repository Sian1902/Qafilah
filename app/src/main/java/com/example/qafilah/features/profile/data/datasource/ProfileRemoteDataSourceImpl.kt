package com.example.qafilah.features.profile.data.datasource

import com.apollographql.apollo.ApolloClient
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.graphql.storefront.CustomerUpdateMutation
import com.example.qafilah.graphql.storefront.GetCustomerQuery
import com.example.qafilah.graphql.storefront.GetPersonalDetailsQuery
import com.example.qafilah.graphql.storefront.type.CustomerUpdateInput

class ProfileRemoteDataSourceImpl(
    private val apolloClient: ApolloClient,
    private val tokenProvider: TokenProvider
) : ProfileRemoteDataSource {

    override suspend fun getCustomerProfile(): GetCustomerQuery.Customer? {
        val token = tokenProvider.getToken() ?: return null
        val response = safeApiCall {
            apolloClient.query(GetCustomerQuery(customerAccessToken = token)).execute()
        }
        return response.customer
    }

    override suspend fun getPersonalDetails(): GetPersonalDetailsQuery.Customer? {
        val token = tokenProvider.getToken() ?: return null
        val response = safeApiCall {
            apolloClient.query(GetPersonalDetailsQuery(customerAccessToken = token)).execute()
        }
        return response.customer
    }

    override suspend fun updateCustomer(
        customerInput: CustomerUpdateInput
    ): CustomerUpdateMutation.CustomerUpdate? {
        val token = tokenProvider.getToken() ?: return null
        val response = safeApiCall {
            apolloClient.mutation(
                CustomerUpdateMutation(
                    customerAccessToken = token,
                    customer = customerInput
                )
            ).execute()
        }
        return response.customerUpdate
    }
}
