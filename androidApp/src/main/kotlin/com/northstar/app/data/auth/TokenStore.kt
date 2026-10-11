package com.northstar.app.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

private val Context.authDataStore by preferencesDataStore(name = "north_star_auth")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.authDataStore

    @Volatile
    var cachedToken: String? = null
        private set

    suspend fun save(token: String) {
        cachedToken = token
        dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
        }
    }

    suspend fun load() {
        val prefs = dataStore.data.first()
        cachedToken = prefs[KEY_TOKEN]
    }

    suspend fun token(): String? {
        cachedToken?.let { return it }
        load()
        return cachedToken
    }

    suspend fun clear() {
        cachedToken = null
        dataStore.edit { it.clear() }
    }

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("jwt")
    }
}
