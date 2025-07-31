package projeto.tinywins.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.PlayerStats
import projeto.tinywins.data.TaskType
import projeto.tinywins.data.auth.Resource

data class CalculatedStats(
    val habitCount: Int = 0,
    val todoCount: Int = 0,
    val completedCount: Int = 0,
    val favoriteCount: Int = 0
)

// Novo UiState para a tela de perfil
sealed interface ProfileScreenUiState {
    object Idle : ProfileScreenUiState
    object Loading : ProfileScreenUiState
    object Success : ProfileScreenUiState
    data class Error(val message: String) : ProfileScreenUiState
}

data class ProfileDataState(
    val playerStats: PlayerStats? = null,
    val calculatedStats: CalculatedStats = CalculatedStats()
)

class ProfileViewModel(private val repository: FirebaseRepository) : ViewModel() {

    private val _screenUiState = MutableStateFlow<ProfileScreenUiState>(ProfileScreenUiState.Idle)
    val screenUiState = _screenUiState.asStateFlow()

    val dataState: StateFlow<ProfileDataState> =
        combine(
            repository.getPlayerStats(),
            repository.getChallenges()
        ) { stats, challenges ->
            val calculated = CalculatedStats(
                habitCount = challenges.count { it.type == TaskType.HABIT },
                todoCount = challenges.count { it.type == TaskType.TODO },
                completedCount = challenges.count { it.isCompleted },
                favoriteCount = challenges.count { it.isFavorite }
            )
            ProfileDataState(playerStats = stats, calculatedStats = calculated)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileDataState()
        )

    fun updateProfile(name: String, imageUri: Uri?) {
        viewModelScope.launch {
            _screenUiState.value = ProfileScreenUiState.Loading
            if (imageUri != null) {
                when (val result = repository.uploadProfileImage(imageUri)) {
                    is Resource.Success -> {
                        handleProfileUpdate(repository.updateUserProfile(name, result.data.toString()))
                    }
                    is Resource.Error -> {
                        _screenUiState.value = ProfileScreenUiState.Error(result.message)
                    }
                }
            } else {
                handleProfileUpdate(repository.updateUserProfile(name, null))
            }
        }
    }

    private fun handleProfileUpdate(result: Resource<Unit>) {
        _screenUiState.value = when (result) {
            is Resource.Success -> ProfileScreenUiState.Success
            is Resource.Error -> ProfileScreenUiState.Error(result.message)
        }
    }

    fun resetScreenState() {
        _screenUiState.value = ProfileScreenUiState.Idle
    }
}