package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import projeto.tinywins.data.auth.AuthRepository
import projeto.tinywins.data.auth.Resource

// Estados da UI para a tela de Cadastro
sealed interface RegistrationUiState {
    object Idle : RegistrationUiState
    object Loading : RegistrationUiState
    data class Success(val authResult: AuthResult) : RegistrationUiState
    data class Error(val message: String) : RegistrationUiState
}

class RegistrationViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun registerUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = RegistrationUiState.Error("Email e senha não podem estar em branco.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegistrationUiState.Loading
            val result = repository.registerUser(email, password)
            _uiState.value = when (result) {
                is Resource.Success -> RegistrationUiState.Success(result.data)
                is Resource.Error -> RegistrationUiState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = RegistrationUiState.Idle
    }
}