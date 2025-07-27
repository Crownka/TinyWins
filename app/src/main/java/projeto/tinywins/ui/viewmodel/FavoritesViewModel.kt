package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.TinyWinChallenge

// Criamos um UiState específico para a tela de Favoritos para clareza.
sealed interface FavoritesUiState {
    data class Success(val challenges: List<TinyWinChallenge>) : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
    object Loading : FavoritesUiState
}

class FavoritesViewModel(private val repository: FirebaseRepository) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = repository.getFavoriteChallenges()
        .map<List<TinyWinChallenge>, FavoritesUiState> { challenges ->
            FavoritesUiState.Success(challenges)
        }
        .catch { exception ->
            emit(FavoritesUiState.Error(exception.message ?: "Ocorreu um erro."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState.Loading
        )
}