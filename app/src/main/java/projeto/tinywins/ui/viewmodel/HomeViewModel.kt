package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.sampleChallenges

sealed interface HomeUiState {
    data class Success(val challenges: List<TinyWinChallenge>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    object Loading : HomeUiState
}

class HomeViewModel(private val repository: FirebaseRepository) : ViewModel() {

    val isOnline: StateFlow<Boolean>
    val uiState: StateFlow<HomeUiState>

    init {
        isOnline = repository.isOnline.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

        uiState = repository.getChallenges()
            // A CORREÇÃO ESTÁ AQUI: Especificamos o tipo de retorno geral <HomeUiState>
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

        // Lógica para o seed automático (continua a mesma)
        viewModelScope.launch {
            // Usamos um pequeno delay para garantir que o uiState inicial já foi coletado
            kotlinx.coroutines.delay(1000)
            if (isOnline.first() && (uiState.value as? HomeUiState.Success)?.challenges?.isEmpty() == true) {
                seedDatabase()
            }
        }
    }

    private fun seedDatabase() {
        viewModelScope.launch {
            sampleChallenges.forEach { challenge ->
                repository.updateChallenge(challenge.id, challenge)
            }
        }
    }
}