package com.example.qafilah.core.network

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A reusable utility to handle threading and error catching for Apollo GraphQL calls.
 */
suspend fun <D : Operation.Data> safeApiCall(
    apiCall: suspend () -> ApolloResponse<D>
): D {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown GraphQL Error"
                throw Exception(errorMessage)
            }

            response.data ?: throw Exception("Response data is null")

        } catch (e: Exception) {
            throw e
        }
    }
}