package com.vidiqalternative.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidiqalternative.data.repository.ApiKeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository
) : ViewModel() {

    private val _youtubeKey = MutableStateFlow("")
    val youtubeKey: StateFlow<String> = _youtubeKey.asStateFlow()

    private val _openrouterKey = MutableStateFlow("")
    val openrouterKey: StateFlow<String> = _openrouterKey.asStateFlow()

    private val _isSetupComplete = MutableStateFlow(false)
    val isSetupComplete: StateFlow<Boolean> = _isSetupComplete.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            apiKeyRepository.isSetupComplete.collect { complete ->
                _isSetupComplete.value = complete
            }
        }
    }

    fun updateYoutubeKey(key: String) {
        _youtubeKey.value = key
    }

    fun updateOpenrouterKey(key: String) {
        _openrouterKey.value = key
    }

    fun saveAndContinue() {
        val ytKey = _youtubeKey.value.trim()
        val orKey = _openrouterKey.value.trim()

        if (ytKey.isBlank() || orKey.isBlank()) {
            _errorMessage.value = "Lütfen her iki API key'i de girin"
            return
        }

        viewModelScope.launch {
            apiKeyRepository.saveKeys(ytKey, orKey)
            _isSetupComplete.value = true
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
