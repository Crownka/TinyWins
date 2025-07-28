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
import projeto.tinywins.data.PlayerStats
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.auth.AuthRepository

sealed interface HomeUiState {
    data class Success(val challenges: List<TinyWinChallenge>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    object Loading : HomeUiState
}

class HomeViewModel(
    private val firebaseRepository: FirebaseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val isOnline: StateFlow<Boolean>
    val uiState: StateFlow<HomeUiState>
    val playerStats: StateFlow<PlayerStats?>

    init {
        isOnline = firebaseRepository.isOnline.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

        uiState = firebaseRepository.getChallenges()
            .map<List<TinyWinChallenge>, HomeUiState> { challenges ->
                HomeUiState.Success(challenges)
            }
            .catch { exception ->
                emit(HomeUiState.Error(exception.message ?: "Ocorreu um erro desconhecido."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeUiState.Loading
            )

        playerStats = firebaseRepository.getPlayerStats().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun onChallengeActionPerformed(challenge: TinyWinChallenge, isPositive: Boolean) {
        viewModelScope.launch {
            // Chama a nova função refatorada no repositório
            firebaseRepository.processChallengeAction(challenge, isPositiveAction = isPositive)
        }
    }
}