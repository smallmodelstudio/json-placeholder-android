package uk.co.fredjames.jsonplaceholder.core.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Manages saving and loading the app's theme mode. */
@Singleton
class ThemeRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val themeModeKey = stringPreferencesKey("theme_mode")

        /** Emits the current [ThemeMode] selection, defaulting to [ThemeMode.SYSTEM]. */
        val themeMode: Flow<ThemeMode> =
            context.dataStore.data.map { preferences ->
                val name = preferences[themeModeKey]
                try {
                    if (name != null) ThemeMode.valueOf(name) else ThemeMode.SYSTEM
                } catch (_: IllegalArgumentException) {
                    ThemeMode.SYSTEM
                }
            }

        /** Updates the selected [ThemeMode]. */
        suspend fun setThemeMode(mode: ThemeMode) {
            context.dataStore.edit { preferences ->
                preferences[themeModeKey] = mode.name
            }
        }
    }
