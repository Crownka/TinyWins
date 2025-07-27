package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.auth.AuthRepository
import projeto.tinywins.data.sampleChallenges

sealed interface HomeUiState {
    data class Success(val challenges: List<TinyWinChallenge>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    object Loading : HomeUiState
}

// O ViewModel agora recebe os dois repositórios
class HomeViewModel(
    private val firebaseRepository: FirebaseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val isOnline: StateFlow<Boolean>
    val uiState: StateFlow<HomeUiState>

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

        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null && isOnline.first() && (uiState.value as? HomeUiState.Success)?.challenges?.isEmpty() == true) {
                seedDatabaseForCurrentUser()
            }
        }
    }

    private fun seedDatabaseForCurrentUser() {
        viewModelScope.launch {
            sampleChallenges.forEach { challenge ->
                firebaseRepository.updateChallenge(challenge.id, challenge)
            }
        }
    }

    // Nova função para fazer logout
    fun signOut() {
        authRepository.signOut()
    }
}