package com.example.qafilah.features.profile.data.datasource

import com.apollographql.apollo.ApolloClient
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.graphql.storefront.CustomerUpdateMutation
import com.example.qafilah.graphql.storefront.GetCustomerQuery
import com.example.qafilah.graphql.storefront.GetPersonalDetailsQuery
import com.example.qafilah.graphql.storefront.type.CustomerUpdateInput

class ProfileRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : ProfileRemoteDataSource {

    override suspend fun getCustomerProfile(customerAccessToken: String): GetCustomerQuery.Customer? {
        val response = safeApiCall {
            apolloClient.query(GetCustomerQuery(customerAccessToken = customerAccessToken)).execute()
        }
        return response.customer
    }

    override suspend fun getPersonalDetails(customerAccessToken: String): GetPersonalDetailsQuery.Customer? {
        val response = safeApiCall {
            apolloClient.query(GetPersonalDetailsQuery(customerAccessToken = customerAccessToken)).execute()
        }
        return response.customer
    }

    override suspend fun updateCustomer(
        customerAccessToken: String,
        customerInput: CustomerUpdateInput
    ): CustomerUpdateMutation.CustomerUpdate? {
        val response = safeApiCall {
            apolloClient.mutation(
                CustomerUpdateMutation(
                    customerAccessToken = customerAccessToken,
                    customer = customerInput
                )
            ).execute()
        }
        return response.customerUpdate
    }
}
