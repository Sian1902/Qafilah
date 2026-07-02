package com.example.qafilah.features.search.data.datasource

interface SearchLocalDataSource {
    fun getRecentSearches(): List<String>
    fun saveSearchQuery(query: String)
    fun removeSearchQuery(query: String)
    fun clearAll()
}