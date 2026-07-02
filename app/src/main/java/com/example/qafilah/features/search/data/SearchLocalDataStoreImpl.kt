package com.example.qafilah.features.search.data
import android.content.SharedPreferences
import com.example.qafilah.features.search.data.datasource.SearchLocalDataSource

class SearchLocalDataSourceImpl(
    private val sharedPreferences: SharedPreferences
) : SearchLocalDataSource {

    private val keyRecentSearches = "recent_searches_key"

    override fun getRecentSearches(): List<String> {
        val rawString = sharedPreferences.getString(keyRecentSearches, "") ?: ""
        if (rawString.isEmpty()) return emptyList()
        return rawString.split("|||")
    }

    override fun saveSearchQuery(query: String) {
        if (query.isBlank()) return
        val currentList = getRecentSearches().toMutableList()
        currentList.remove(query)
        currentList.add(0, query)

        val cappedList = currentList.take(3)

        sharedPreferences.edit()
            .putString(keyRecentSearches, cappedList.joinToString("|||"))
            .apply()
    }

    override fun removeSearchQuery(query: String) {
        val currentList = getRecentSearches().toMutableList()
        if (currentList.remove(query)) {
            sharedPreferences.edit()
                .putString(keyRecentSearches, currentList.joinToString("|||"))
                .apply()
        }
    }

    override fun clearAll() {
        sharedPreferences.edit().remove(keyRecentSearches).apply()
    }
}