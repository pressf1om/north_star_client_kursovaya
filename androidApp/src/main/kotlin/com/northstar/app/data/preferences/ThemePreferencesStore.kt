package com.northstar.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "north_star_theme")

@Singleton
class ThemePreferencesStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.themeDataStore

    val darkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DARK_THEME_KEY] ?: false
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[DARK_THEME_KEY] = enabled
        }
    }

    private companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }
}
