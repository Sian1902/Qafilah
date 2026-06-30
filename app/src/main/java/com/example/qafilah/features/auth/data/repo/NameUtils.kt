package com.example.qafilah.features.auth.data.repo

/**
 * Helper object for name extraction so callers can reference NameUtils.extractNames(...).
 */
object NameUtils {
    fun extractNames(
        displayName: String?,
        email: String?,
        defaultFirst: String? = null,
        defaultLast: String? = null
    ): Pair<String?, String?> {
        val fullName = displayName?.takeIf { it.isNotBlank() } ?: email?.substringBefore("@") ?: ""
        val parts = fullName.split(" ", limit = 2)
        val first = parts.getOrNull(0)?.takeIf { it.isNotBlank() } ?: defaultFirst
        val last = parts.getOrNull(1)?.takeIf { it.isNotBlank() } ?: defaultLast
        return Pair(first, last)
    }
}

