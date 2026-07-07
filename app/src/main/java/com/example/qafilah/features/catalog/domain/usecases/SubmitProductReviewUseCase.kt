package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.features.catalog.domain.model.SubmitReviewParams
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository
import com.example.qafilah.features.profile.domain.repository.ProfileRepository

class SubmitProductReviewUseCase(
    private val repository: CatalogRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        productId: String,
        rating: Int,
        title: String,
        body: String,
        dateString: String
    ): Result<Unit> {

        val user = try {
            profileRepository.getPersonalDetails()
        } catch (e: Exception) {
            null
        }

        val customerName = user?.fold(
            onSuccess = { "${it.firstName} ${it.lastName}" },
            onFailure = { "Anonymous" }
        ) ?: "Anonymous"

        val params = SubmitReviewParams(
            productId = productId,
            customerName = customerName,
            rating = rating,
            title = title,
            body = body,
            dateString = dateString
        )

        return repository.submitProductReview(params)
    }
}