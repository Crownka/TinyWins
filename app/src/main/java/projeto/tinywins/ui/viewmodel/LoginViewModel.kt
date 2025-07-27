package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import projeto.tinywins.data.auth.AuthRepository
import projeto.tinywins.data.auth.Resource

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val authResult: AuthResult) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Email e senha não podem estar em branco.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = repository.loginUser(email, password)
            _uiState.value = when (result) {
                is Resource.Success -> LoginUiState.Success(result.data)
                is Resource.Error -> LoginUiState.Error(result.message)
            }
        }
    }

    // NOVA FUNÇÃO para login com Google
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = repository.signInWithGoogle(idToken)
            _uiState.value = when (result) {
                is Resource.Success -> LoginUiState.Success(result.data)
                is Resource.Error -> LoginUiState.Error(result.message)
            }
        }
    }

    // Seta o estado para Loading, usado pelo botão do Google para mostrar o spinner
    fun setLoading() {
        _uiState.value = LoginUiState.Loading
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}