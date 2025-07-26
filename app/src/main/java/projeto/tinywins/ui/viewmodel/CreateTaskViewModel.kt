package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.TinyWinChallenge

// Estados da UI para a tela de criação
sealed interface CreateTaskUiState {
    object Idle : CreateTaskUiState // Estado inicial
    object Loading : CreateTaskUiState // Salvando no Firebase
    object Success : CreateTaskUiState // Salvo com sucesso
}

class CreateTaskViewModel(private val repository: FirebaseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateTaskUiState>(CreateTaskUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun createChallenge(challenge: TinyWinChallenge) {
        viewModelScope.launch {
            _uiState.value = CreateTaskUiState.Loading
            try {
                // Usamos updateChallenge para garantir o ID que geramos via UUID.
                // O .set() no Firestore cria o documento se ele não existir.
                repository.updateChallenge(challenge.id, challenge)
                _uiState.value = CreateTaskUiState.Success
            } catch (e: Exception) {
                // Em um app real, trataríamos o erro aqui
                println("Erro ao criar desafio: ${e.message}")
                _uiState.value = CreateTaskUiState.Idle // Volta ao estado inicial em caso de erro
            }
        }
    }
}