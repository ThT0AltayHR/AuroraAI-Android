package com.auroraai.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.auroraai.app.data.Message
import com.auroraai.app.data.Role
import com.auroraai.app.data.SettingsStore
import com.auroraai.app.network.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val settings: SettingsStore) : ViewModel() {
    private val repo = ChatRepository(settings)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun send(text: String) {
        val userMsg = Message(role = Role.USER, content = text)
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reply = repo.sendMessage(_messages.value)
                _messages.value = _messages.value + Message(role = Role.ASSISTANT, content = reply)
            } catch (e: Exception) {
                _messages.value = _messages.value + Message(
                    role = Role.ASSISTANT,
                    content = "Hata: ${e.message ?: "bilinmeyen bir sorun oluştu"}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        fun factory(settings: SettingsStore) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ChatViewModel(settings) as T
        }
    }
}
