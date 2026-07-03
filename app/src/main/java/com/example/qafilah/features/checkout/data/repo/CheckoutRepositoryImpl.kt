package com.example.qafilah.features.checkout.data.repo

import com.example.qafilah.features.checkout.data.datasource.CheckoutRemoteDataSource
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository

class CheckoutRepositoryImpl(
    private val remoteDataSource: CheckoutRemoteDataSource
): CheckoutRepository {
}