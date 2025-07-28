package projeto.tinywins.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.PlayerStats
import projeto.tinywins.data.auth.Resource

class ProfileViewModel(private val repository: FirebaseRepository) : ViewModel() {

    val playerStats: StateFlow<PlayerStats?> = repository.getPlayerStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateProfile(name: String, imageUri: Uri?) {
        viewModelScope.launch {
            if (imageUri != null) {
                // Primeiro, faz o upload da imagem
                when (val result = repository.uploadProfileImage(imageUri)) {
                    is Resource.Success -> {
                        // Se o upload for bem-sucedido, atualiza o perfil com a nova URL da imagem
                        repository.updateUserProfile(name, result.data.toString())
                    }
                    is Resource.Error -> {
                        // TODO: Mostrar um erro para o usuário informando que o upload falhou
                        println("Erro no upload: ${result.message}")
                    }
                }
            } else {
                // Se não houver nova imagem, apenas atualiza o nome
                repository.updateUserProfile(name, null)
            }
        }
    }
}