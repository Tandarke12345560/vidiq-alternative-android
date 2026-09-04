package com.vidiqalternative.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class ApiKeyRepository @Inject constructor(
    private val context: Context
) {
    companion object {
        val YOUTUBE_API_KEY = stringPreferencesKey("youtube_api_key")
        val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
    }

    val youtubeApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[YOUTUBE_API_KEY] ?: ""
    }

    val openrouterApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[OPENROUTER_API_KEY] ?: ""
    }

    val isSetupComplete: Flow<Boolean> = context.dataStore.data.map { prefs ->
        val yt = prefs[YOUTUBE_API_KEY] ?: ""
        val or = prefs[OPENROUTER_API_KEY] ?: ""
        yt.isNotBlank() && or.isNotBlank()
    }

    suspend fun saveKeys(youtubeKey: String, openrouterKey: String) {
        context.dataStore.edit { prefs ->
            prefs[YOUTUBE_API_KEY] = youtubeKey
            prefs[OPENROUTER_API_KEY] = openrouterKey
        }
    }
}
