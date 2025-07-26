package projeto.tinywins.data.auth

// Classe genérica para encapsular o resultado de operações que podem falhar.
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}