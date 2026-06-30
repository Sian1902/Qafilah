package com.example.qafilah.features.auth.data.repo

object NameUtils {
    fun extractNames(
        displayName: String?,
        email: String?,
        defaultFirst: String? = null,
        defaultLast: String? = null
    ): Pair<String?, String?> {
        val rawName = displayName?.takeIf { it.isNotBlank() }
            ?: email?.substringBefore("@")?.replace(Regex("[._-]"), " ")
            ?: ""

        val cleanName = rawName.trim().replace(Regex("\\s+"), " ")

        if (cleanName.isBlank()) {
            return Pair(defaultFirst, defaultLast)
        }

        val parts = cleanName.split(" ", limit = 2)

        val first = parts.getOrNull(0)
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            ?: defaultFirst

        val last = parts.getOrNull(1)
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            ?: defaultLast

        return Pair(first, last)
    }
}