package com.vidiqalternative.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidiqalternative.data.api.Message
import com.vidiqalternative.data.api.ModelDto
import com.vidiqalternative.data.repository.AIRepository
import com.vidiqalternative.data.repository.ModelRepository
import com.vidiqalternative.data.repository.ModelStats
import com.vidiqalternative.util.SystemPrompt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: String,
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)

@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val modelRepository: ModelRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedModel = MutableStateFlow("mistralai/mistral-7b-instruct")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _availableModels = MutableStateFlow<List<ModelDto>>(emptyList())
    val availableModels: StateFlow<List<ModelDto>> = _availableModels.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _modelStats = MutableStateFlow<ModelStats?>(null)
    val modelStats: StateFlow<ModelStats?> = _modelStats.asStateFlow()

    private val _isRefreshingModels = MutableStateFlow(false)
    val isRefreshingModels: StateFlow<Boolean> = _isRefreshingModels.asStateFlow()

    private val conversationHistory = mutableListOf<Message>()

    init {
        loadSelectedModel()
        loadAvailableModels()
        loadApiKey()
    }

    private fun loadSelectedModel() {
        viewModelScope.launch {
            modelRepository.getSelectedModel().collect { model ->
                _selectedModel.value = model
            }
        }
    }

    private fun loadApiKey() {
        viewModelScope.launch {
            modelRepository.getApiKey().collect { key ->
                _apiKey.value = key
                if (key.isNotBlank()) {
                    loadAvailableModels(forceRefresh = true)
                }
            }
        }
    }

    private fun loadAvailableModels(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isRefreshingModels.value = true
            modelRepository.getAvailableModels(forceRefresh)
                .onSuccess { models ->
                    _availableModels.value = models
                    _modelStats.value = modelRepository.getModelStats()
                }
                .onFailure { error ->
                    _errorMessage.value = "Modeller yüklenemedi: ${error.message}"
                }
            _isRefreshingModels.value = false
        }
    }

    fun setApiKey(key: String) {
        viewModelScope.launch {
            modelRepository.setApiKey(key)
            _apiKey.value = key
            loadAvailableModels(forceRefresh = true)
        }
    }

    fun selectModel(modelId: String) {
        viewModelScope.launch {
            modelRepository.setSelectedModel(modelId)
            _selectedModel.value = modelId
        }
    }

    fun refreshModels() {
        loadAvailableModels(forceRefresh = true)
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val userMessage = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            role = "user",
            content = content
        )

        _messages.value = _messages.value + userMessage

        val loadingMessage = ChatMessage(
            id = "loading_${System.currentTimeMillis()}",
            role = "assistant",
            content = "",
            isLoading = true
        )
        _messages.value = _messages.value + loadingMessage
        _isLoading.value = true

        viewModelScope.launch {
            aiRepository.chat(
                userMessage = content,
                conversationHistory = conversationHistory
            )
                .onSuccess { response ->
                    val assistantMessage = ChatMessage(
                        id = "assistant_${System.currentTimeMillis()}",
                        role = "assistant",
                        content = response
                    )
                    _messages.value = _messages.value.filter { it.id != loadingMessage.id } + assistantMessage
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                    _messages.value = _messages.value.filter { it.id != loadingMessage.id }
                }

            _isLoading.value = false
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
        conversationHistory.clear()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun quickAction(action: QuickAction) {
        val prompt = when (action) {
            QuickAction.SEO_ANALYSIS -> "Bir YouTube videosunun SEO analizini yapmak istiyorum. Lütfen analiz için gerekli adımları açıkla."
            QuickAction.CONTENT_IDEAS -> "YouTube için içerik fikirleri üretir misin? Trend konuları ve popüler konuları araştır."
            QuickAction.TITLE_SUGGESTIONS -> "YouTube video başlıkları için optimizasyon önerileri ver. İyi başlık örnekleri göster."
            QuickAction.TAG_SUGGESTIONS -> "YouTube video etiketleri için strateji önerileri ver. Etiket araştırması nasıl yapılır?"
            QuickAction.COMPETITOR_ANALYSIS -> "YouTube rakip analizi nasıl yapılır? Rakip kanalları analiz etme yöntemlerini açıkla."
            QuickAction.TREND_TOPICS -> "YouTube'da şu an trend olan konuları araştır ve öner."
        }
        sendMessage(prompt)
    }
}

enum class QuickAction(val title: String, val icon: String) {
    SEO_ANALYSIS("SEO Analiz", "🔍"),
    CONTENT_IDEAS("İçerik Fikirleri", "💡"),
    TITLE_SUGGESTIONS("Başlık Önerileri", "📝"),
    TAG_SUGGESTIONS("Etiket Önerileri", "🏷️"),
    COMPETITOR_ANALYSIS("Rakip Analizi", "📊"),
    TREND_TOPICS("Trend Konular", "🔥")
}
