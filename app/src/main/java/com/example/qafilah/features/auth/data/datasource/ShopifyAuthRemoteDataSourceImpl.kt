package com.example.qafilah.features.auth.data.datasource

import com.apollographql.apollo.ApolloClient
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.graphql.storefront.GetCustomerQuery
import com.example.qafilah.graphql.storefront.LoginCustomerMutation
import com.example.qafilah.graphql.storefront.LogoutCustomerMutation
import com.example.qafilah.graphql.storefront.RegisterCustomerMutation
import com.example.qafilah.graphql.storefront.type.CustomerAccessTokenCreateInput
import com.example.qafilah.graphql.storefront.type.CustomerCreateInput

class ShopifyAuthRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : ShopifyAuthRemoteDataSource {

    override suspend fun registerCustomer(input: CustomerCreateInput): RegisterCustomerMutation.CustomerCreate? {
        val response = safeApiCall {
            apolloClient.mutation(RegisterCustomerMutation(input = input)).execute()
        }
        return response.customerCreate
    }

    override suspend fun loginCustomer(input: CustomerAccessTokenCreateInput): LoginCustomerMutation.CustomerAccessTokenCreate? {
        val response = safeApiCall {
            apolloClient.mutation(LoginCustomerMutation(input = input)).execute()
        }
        return response.customerAccessTokenCreate
    }

    override suspend fun logoutCustomer(customerAccessToken: String): LogoutCustomerMutation.CustomerAccessTokenDelete? {
        val response = safeApiCall {
            apolloClient.mutation(LogoutCustomerMutation(customerAccessToken = customerAccessToken)).execute()
        }
        return response.customerAccessTokenDelete
    }

    override suspend fun getCustomerProfile(customerAccessToken: String): GetCustomerQuery.Customer? {
        val response = safeApiCall {
            apolloClient.query(GetCustomerQuery(customerAccessToken = customerAccessToken)).execute()
        }
        return response.customer
    }
}