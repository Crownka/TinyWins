package projeto.tinywins.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.PlayerStats
import projeto.tinywins.data.TaskType
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.auth.Resource

data class CalculatedStats(
    val habitCount: Int = 0,
    val todoCount: Int = 0,
    val completedCount: Int = 0,
    val favoriteCount: Int = 0
)

data class ProfileUiState(
    val playerStats: PlayerStats? = null,
    val calculatedStats: CalculatedStats = CalculatedStats()
)

class ProfileViewModel(private val repository: FirebaseRepository) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> =
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
            ProfileUiState(playerStats = stats, calculatedStats = calculated)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState()
        )

    fun updateProfile(name: String, imageUri: Uri?) {
        viewModelScope.launch {
            if (imageUri != null) {
                when (val result = repository.uploadProfileImage(imageUri)) {
                    is Resource.Success -> {
                        repository.updateUserProfile(name, result.data.toString())
                    }
                    is Resource.Error -> { /* Tratar erro de upload */ }
                }
            } else {
                repository.updateUserProfile(name, null)
            }
        }
    }
}