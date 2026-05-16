package com.picker.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picker.app.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val login: String = "",
    val pin: String = "",
    val loading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(private val auth: AuthUseCase) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onLoginChange(v: String) { _state.value = _state.value.copy(login = v.trim()) }
    fun onPinChange(v: String) { _state.value = _state.value.copy(pin = v.filter(Char::isDigit).take(8)) }

    fun submit(onSuccess: (String) -> Unit) {
        val s = _state.value
        _state.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val r = auth(s.login, s.pin)) {
                is AuthUseCase.Result.Success -> onSuccess(r.shiftId)
                AuthUseCase.Result.InvalidCredentials ->
                    _state.value = s.copy(loading = false, error = "Неверный логин или PIN")
                AuthUseCase.Result.LockedOut ->
                    _state.value = s.copy(loading = false, error = "Заблокировано: 5 неудачных попыток")
            }
        }
    }
}
