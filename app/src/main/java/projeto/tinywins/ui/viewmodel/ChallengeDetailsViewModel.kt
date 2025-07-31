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
    private val challengeId: String,
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

    fun toggleFavoriteStatus(challenge: TinyWinChallenge) {
        viewModelScope.launch {
            try {
                repository.toggleFavoriteStatus(challenge.id, challenge.isFavorite)
            } catch (e: Exception) {
                println("Erro ao atualizar favorito: ${e.message}")
            }
        }
    }

    fun deleteChallenge() {
        viewModelScope.launch {
            try {
                repository.deleteChallenge(challengeId)
            } catch (e: Exception) {
                println("Erro ao deletar desafio: ${e.message}")
            }
        }
    }
}