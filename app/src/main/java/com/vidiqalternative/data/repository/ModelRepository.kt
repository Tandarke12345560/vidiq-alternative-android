package com.vidiqalternative.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vidiqalternative.data.api.ModelDto
import com.vidiqalternative.data.api.OpenRouterApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "model_settings")

@Singleton
class ModelRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val openRouterService: OpenRouterApiService
) {

    companion object {
        private val SELECTED_MODEL_KEY = stringPreferencesKey("selected_model")
        private val API_KEY_KEY = stringPreferencesKey("api_key")
        private const val DEFAULT_MODEL = "mistralai/mistral-7b-instruct"
    }

    private var cachedModels: List<ModelDto> = emptyList()
    private var lastFetchTime: Long = 0

    suspend fun getAvailableModels(forceRefresh: Boolean = false): Result<List<ModelDto>> =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh && cachedModels.isNotEmpty() &&
                    (System.currentTimeMillis() - lastFetchTime) < 300_000
                ) {
                    return@withContext Result.success(cachedModels)
                }

                val response = openRouterService.getModels()
                if (response.isSuccessful) {
                    val models = response.body()?.data ?: emptyList()
                    cachedModels = models
                    lastFetchTime = System.currentTimeMillis()
                    Result.success(models)
                } else {
                    if (cachedModels.isNotEmpty()) {
                        Result.success(cachedModels)
                    } else {
                        Result.failure(Exception("Model listesi alınamadı: ${response.code()}"))
                    }
                }
            } catch (e: Exception) {
                if (cachedModels.isNotEmpty()) {
                    Result.success(cachedModels)
                } else {
                    Result.failure(e)
                }
            }
        }

    suspend fun getFreeModels(): Result<List<ModelDto>> {
        return getAvailableModels().map { models ->
            models.filter { model ->
                model.pricing?.prompt == "0" ||
                        model.pricing?.prompt == "0.0" ||
                        model.id.contains(":free")
            }
        }
    }

    suspend fun searchModels(query: String): Result<List<ModelDto>> {
        return getAvailableModels().map { models ->
            models.filter { model ->
                model.name.contains(query, ignoreCase = true) ||
                        model.id.contains(query, ignoreCase = true) ||
                        (model.description?.contains(query, ignoreCase = true) == true)
            }
        }
    }

    suspend fun getModelById(modelId: String): Result<ModelDto?> {
        return getAvailableModels().map { models ->
            models.find { it.id == modelId }
        }
    }

    fun getSelectedModel(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[SELECTED_MODEL_KEY] ?: DEFAULT_MODEL
        }
    }

    suspend fun setSelectedModel(modelId: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_MODEL_KEY] = modelId
        }
    }

    suspend fun getSelectedModelSync(): String {
        return context.dataStore.data.first()[SELECTED_MODEL_KEY] ?: DEFAULT_MODEL
    }

    suspend fun getApiKey(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[API_KEY_KEY] ?: ""
        }
    }

    suspend fun getApiKeySync(): String {
        return context.dataStore.data.first()[API_KEY_KEY] ?: ""
    }

    suspend fun setApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[API_KEY_KEY] = apiKey
        }
    }

    suspend fun getModelStats(): ModelStats {
        val models = getAvailableModels().getOrElse { emptyList() }
        return ModelStats(
            totalModels = models.size,
            freeModels = models.count { it.isFree() },
            paidModels = models.count { !it.isFree() },
            textModels = models.count { it.isTextModel() },
            imageModels = models.count { it.isImageModel() }
        )
    }

    private fun ModelDto.isFree(): Boolean {
        return pricing?.prompt == "0" ||
                pricing?.prompt == "0.0" ||
                id.contains(":free")
    }

    private fun ModelDto.isTextModel(): Boolean {
        return architecture?.outputModalities?.contains("text") == true
    }

    private fun ModelDto.isImageModel(): Boolean {
        return architecture?.outputModalities?.contains("image") == true
    }
}

data class ModelStats(
    val totalModels: Int,
    val freeModels: Int,
    val paidModels: Int,
    val textModels: Int,
    val imageModels: Int
)
