package org.sopt.official.example.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.sopt.official.playground.auth.utils.PlaygroundLog
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _loginEventStream = MutableSharedFlow<Unit>()
    val loginEventStream = _loginEventStream.asSharedFlow()

    private val _text = MutableStateFlow("로그인 이전")
    val text = _text.asStateFlow()

    fun completeLogin(text: String) {
        _text.value = text
    }

    fun login() {
        PlaygroundLog.d("login event")
        viewModelScope.launch {
            _loginEventStream.emit(Unit)
        }
    }
}