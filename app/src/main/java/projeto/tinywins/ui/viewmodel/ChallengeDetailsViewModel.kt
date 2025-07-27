package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.TinyWinChallenge

sealed interface DetailsUiState {
    data class Success(val challenge: TinyWinChallenge) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
    object Loading : DetailsUiState
}

class ChallengeDetailsViewModel(
    private val challengeId: String, // Tornamos o ID acessível na classe
    private val repository: FirebaseRepository
) : ViewModel() {

    val uiState: StateFlow<DetailsUiState>

    init {
        uiState = repository.getChallengeById(challengeId)
            .map { challenge ->
                if (challenge != null) {
                    DetailsUiState.Success(challenge)
                } else {
                    DetailsUiState.Error("Desafio não encontrado.")
                }
            }
            .catch { exception ->
                emit(DetailsUiState.Error(exception.message ?: "Ocorreu um erro."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DetailsUiState.Loading
            )
    }

    /**
     * NOVA FUNÇÃO: Chama o repositório para alternar o status de favorito.
     * @param challenge O desafio atual para sabermos seu estado.
     */
    fun toggleFavoriteStatus(challenge: TinyWinChallenge) {
        viewModelScope.launch {
            try {
                repository.toggleFavoriteStatus(challenge.id, challenge.isFavorite)
            } catch (e: Exception) {
                // Em um app real, poderíamos emitir um estado de erro para a UI
                println("Erro ao atualizar favorito: ${e.message}")
            }
        }
    }
}