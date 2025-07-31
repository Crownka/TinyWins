package projeto.tinywins.data.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser
        get() = auth.currentUser

    suspend fun registerUser(email: String, password: String): Resource<AuthResult> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ocorreu um erro ao tentar registrar.")
        }
    }

    suspend fun loginUser(email: String, password: String): Resource<AuthResult> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                    "E-mail ou senha incorretos."
                }
                else -> e.message ?: "Ocorreu um erro ao tentar fazer login."
            }
            Resource.Error(errorMessage)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Resource<AuthResult> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ocorreu um erro ao tentar fazer login com o Google.")
        }
    }

    fun signOut() {
        auth.signOut()
    }
}