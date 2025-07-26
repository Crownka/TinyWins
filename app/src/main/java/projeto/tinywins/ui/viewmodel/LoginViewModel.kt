package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import projeto.tinywins.data.auth.AuthRepository
import projeto.tinywins.data.auth.Resource

// Estados da UI para a tela de Login
sealed interface LoginUiState {
    object Idle : LoginUiState // Estado inicial
    object Loading : LoginUiState // Processando o login
    data class Success(val authResult: AuthResult) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loginUser(email: String, password: String) {
        // Validação simples para evitar chamadas vazias
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

    // Função para resetar o estado, útil para limpar a mensagem de erro
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}