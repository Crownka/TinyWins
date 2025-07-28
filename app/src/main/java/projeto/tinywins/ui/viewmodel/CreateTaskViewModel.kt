package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.TinyWinChallenge

sealed interface CreateTaskUiState {
    object Idle : CreateTaskUiState
    object Loading : CreateTaskUiState
    object Success : CreateTaskUiState
}

class CreateTaskViewModel(private val repository: FirebaseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateTaskUiState>(CreateTaskUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun createChallenge(challenge: TinyWinChallenge) {
        viewModelScope.launch {
            _uiState.value = CreateTaskUiState.Loading
            try {
                repository.addChallenge(challenge)
                _uiState.value = CreateTaskUiState.Success
            } catch (e: Exception) {
                println("Erro ao criar desafio: ${e.message}")
                _uiState.value = CreateTaskUiState.Idle
            }
        }
    }
}