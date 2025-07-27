package projeto.tinywins.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val networkStatusTracker: NetworkStatusTracker) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserChallengesCollection(): CollectionReference? {
        val userId = auth.currentUser?.uid
        return userId?.let {
            db.collection("users").document(it).collection("challenges")
        }
    }

    val isOnline: Flow<Boolean> = networkStatusTracker.isOnline

    // As funções de LEITURA continuam as mesmas
    fun getChallenges(): Flow<List<TinyWinChallenge>> {
        val challengesCollection = getUserChallengesCollection() ?: return emptyFlow()

        return challengesCollection
            .orderBy("createdAt")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects<TinyWinChallenge>()
            }
    }

    fun getChallengeById(challengeId: String): Flow<TinyWinChallenge?> {
        val challengesCollection = getUserChallengesCollection() ?: return emptyFlow()

        return challengesCollection.document(challengeId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject<TinyWinChallenge>()
            }
    }

    fun getFavoriteChallenges(): Flow<List<TinyWinChallenge>> {
        val challengesCollection = getUserChallengesCollection() ?: return emptyFlow()

        return challengesCollection
            .whereEqualTo("isFavorite", true)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects<TinyWinChallenge>()
            }
    }

    // As funções de ESCRITA agora não usam mais .await()
    suspend fun toggleFavoriteStatus(challengeId: String, isCurrentlyFavorite: Boolean) {
        // Apenas enfileira a operação, não espera a conclusão no servidor
        getUserChallengesCollection()?.document(challengeId)?.update("isFavorite", !isCurrentlyFavorite)
    }

    suspend fun addChallenge(challenge: TinyWinChallenge) {
        // Apenas enfileira a operação, não espera a conclusão no servidor
        getUserChallengesCollection()?.add(challenge)
    }

    suspend fun updateChallenge(challengeId: String, challenge: TinyWinChallenge) {
        // Apenas enfileira a operação, não espera a conclusão no servidor
        getUserChallengesCollection()?.document(challengeId)?.set(challenge)
    }

    suspend fun deleteChallenge(challengeId: String) {
        // Apenas enfileira a operação, não espera a conclusão no servidor
        getUserChallengesCollection()?.document(challengeId)?.delete()
    }
}