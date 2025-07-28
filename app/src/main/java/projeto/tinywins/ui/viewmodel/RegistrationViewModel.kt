package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.auth.AuthRepository
import projeto.tinywins.data.auth.Resource

sealed interface RegistrationUiState {
    object Idle : RegistrationUiState
    object Loading : RegistrationUiState
    data class Success(val authResult: AuthResult) : RegistrationUiState
    data class Error(val message: String) : RegistrationUiState
}

class RegistrationViewModel(
    private val authRepository: AuthRepository,
    private val firebaseRepository: FirebaseRepository // Adicionamos o outro repositório
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun registerUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = RegistrationUiState.Error("Email e senha não podem estar em branco.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegistrationUiState.Loading
            val result = authRepository.registerUser(email, password)

            if (result is Resource.Success) {
                // Se o registro for bem-sucedido, cria os dados iniciais do usuário
                firebaseRepository.createInitialUserDataIfNeeded()
                _uiState.value = RegistrationUiState.Success(result.data)
            } else if (result is Resource.Error) {
                _uiState.value = RegistrationUiState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = RegistrationUiState.Idle
    }
}