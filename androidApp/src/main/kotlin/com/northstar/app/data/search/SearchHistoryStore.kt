package com.northstar.app.data.search

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

private val Context.searchDataStore by preferencesDataStore(name = "north_star_search")

enum class SearchScope(val key: String) {
    PEOPLE("people"),
    FLEET("fleet"),
}

@Singleton
class SearchHistoryStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.searchDataStore

    suspend fun history(scope: SearchScope): List<String> {
        val raw = dataStore.data.first()[keyFor(scope)] ?: return emptyList()
        if (raw.isBlank()) return emptyList()
        return raw.split(HISTORY_DELIMITER).filter { it.isNotBlank() }
    }

    suspend fun addQuery(scope: SearchScope, query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val current = history(scope).filterNot { it.equals(trimmed, ignoreCase = true) }
        val updated = listOf(trimmed) + current
        val capped = updated.take(MAX_HISTORY)
        dataStore.edit { prefs ->
            prefs[keyFor(scope)] = capped.joinToString(HISTORY_DELIMITER)
        }
    }

    suspend fun clear(scope: SearchScope) {
        dataStore.edit { prefs ->
            prefs.remove(keyFor(scope))
        }
    }

    private fun keyFor(scope: SearchScope) = stringPreferencesKey("history_${scope.key}")

    private companion object {
        const val MAX_HISTORY = 10
        const val HISTORY_DELIMITER = "\u001F"
    }
}
