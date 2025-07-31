package projeto.tinywins.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository

class SettingsViewModel(private val repository: FirebaseRepository) : ViewModel() {

    fun clearAllFavorites() {
        viewModelScope.launch {
            repository.clearAllFavorites()
        }
    }

    fun deleteAllHabits() {
        viewModelScope.launch {
            repository.deleteAllHabits()
        }
    }

    fun deleteAllTodos() {
        viewModelScope.launch {
            repository.deleteAllTodos()
        }
    }
}