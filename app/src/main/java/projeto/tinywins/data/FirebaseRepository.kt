package projeto.tinywins.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val networkStatusTracker: NetworkStatusTracker) {

    private val db = FirebaseFirestore.getInstance()
    private val challengesCollection = db.collection("challenges")

    // Expõe o status da conexão para o ViewModel
    val isOnline: Flow<Boolean> = networkStatusTracker.isOnline

    /**
     * Retorna um Flow que emite a lista de desafios sempre que houver uma
     * atualização no Firestore. Isso nos dá dados em tempo real.
     */
    fun getChallenges(): Flow<List<TinyWinChallenge>> {
        return challengesCollection
            .orderBy("createdAt") // Ordena os desafios pela data de criação
            .snapshots() // Cria o listener em tempo real
            .map { snapshot ->
                // Converte a resposta do Firestore para nossa lista de data classes
                snapshot.toObjects<TinyWinChallenge>()
            }
    }

    /**
     * Adiciona um novo desafio ao Firestore.
     */
    suspend fun addChallenge(challenge: TinyWinChallenge) {
        challengesCollection.add(challenge).await()
    }

    /**
     * Atualiza um desafio existente no Firestore.
     * @param challengeId O ID do documento a ser atualizado.
     * @param challenge O objeto com os novos dados.
     */
    suspend fun updateChallenge(challengeId: String, challenge: TinyWinChallenge) {
        challengesCollection.document(challengeId).set(challenge).await()
    }

    /**
     * Deleta um desafio do Firestore.
     * @param challengeId O ID do documento a ser deletado.
     */
    suspend fun deleteChallenge(challengeId: String) {
        challengesCollection.document(challengeId).delete().await()
    }
}