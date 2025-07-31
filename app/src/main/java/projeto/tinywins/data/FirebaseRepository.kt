package projeto.tinywins.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import projeto.tinywins.data.auth.Resource
import java.util.Calendar
import java.util.Date

class FirebaseRepository(private val networkStatusTracker: NetworkStatusTracker) {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserDocument() = auth.currentUser?.uid?.let { db.collection("users").document(it) }

    private fun getUserChallengesCollection(): CollectionReference? = getUserDocument()?.collection("challenges")

    private fun getUserStorageRef() = auth.currentUser?.uid?.let { storage.reference.child("profile_pictures/$it") }

    val isOnline: Flow<Boolean> = networkStatusTracker.isOnline

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

    suspend fun createInitialUserDataIfNeeded() {
        getUserDocument()?.let { userDoc ->
            if (userDoc.get().await().exists()) {
                return
            }
            val initialStats = PlayerStats(userId = userDoc.id, displayName = "Novo Jogador")
            userDoc.set(initialStats).await()
        }
    }

    suspend fun processChallengeAction(challenge: TinyWinChallenge, isPositiveAction: Boolean) {
        val userDoc = getUserDocument() ?: return
        val challengeDocRef = if (challenge.id.isNotBlank()) getUserChallengesCollection()?.document(challenge.id) else null

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userDoc)
            val currentStats = userSnapshot.toObject(PlayerStats::class.java) ?: return@runTransaction

            if (challenge.type == TaskType.HABIT && challenge.isHabitCompleted) {
                return@runTransaction
            }

            if (isPositiveAction) {
                if (challenge.type == TaskType.TODO) {
                    if (!challenge.isCompleted && challengeDocRef != null) {
                        transaction.update(userDoc, "xp", FieldValue.increment(challenge.xp.toLong()))
                        transaction.update(userDoc, "coins", FieldValue.increment(challenge.coins.toLong()))
                        transaction.update(challengeDocRef, "isCompleted", true)
                    }
                } else if (challenge.type == TaskType.HABIT) {
                    if (!isSameDay(challenge.lastCompletedTimestamp, Date())) {
                        var newHealth = (currentStats.health + 5).coerceIn(0, currentStats.maxHealth)
                        var newXp = currentStats.xp + challenge.xp
                        var newCoins = currentStats.coins + challenge.coins
                        var newLevel = currentStats.level
                        var newDiamonds = currentStats.diamonds
                        var newHabitProgress = challenge.habitCurrentProgress

                        if (challenge.habitDurationInDays > 0) {
                            val incrementDays = when (challenge.resetFrequency) {
                                ResetFrequency.WEEKLY -> 7
                                ResetFrequency.MONTHLY -> 30
                                else -> 1
                            }
                            newHabitProgress += incrementDays

                            if (newHabitProgress >= challenge.habitDurationInDays) {
                                newHabitProgress = challenge.habitDurationInDays
                                transaction.update(challengeDocRef!!, "isHabitCompleted", true)

                                // CÁLCULO DO BÔNUS DINÂMICO
                                val difficultyMultiplier = (challenge.difficulty.ordinal + 1) * 1.5
                                val frequencyMultiplier = when (challenge.resetFrequency) {
                                    ResetFrequency.WEEKLY -> 1.2
                                    ResetFrequency.MONTHLY -> 1.5
                                    else -> 1.0
                                }
                                val bonusXp = (challenge.habitDurationInDays * difficultyMultiplier * frequencyMultiplier * 2).toInt()
                                val bonusCoins = (bonusXp / 2).coerceAtLeast(20)
                                val bonusDiamonds = (challenge.habitDurationInDays / 10).coerceAtLeast(1)

                                newXp += bonusXp
                                newCoins += bonusCoins
                                newDiamonds += bonusDiamonds
                            }
                            transaction.update(challengeDocRef!!, "habitCurrentProgress", newHabitProgress)
                        }

                        val xpToNextLvl = currentStats.xpToNextLevel()
                        if (newXp >= xpToNextLvl) {
                            newXp -= xpToNextLvl
                            newLevel += 1
                            newHealth = currentStats.maxHealth
                            newDiamonds += 1
                        }

                        transaction.update(userDoc, mapOf(
                            "xp" to newXp,
                            "coins" to newCoins,
                            "health" to newHealth,
                            "level" to newLevel,
                            "diamonds" to newDiamonds
                        ))

                        transaction.update(challengeDocRef!!, "lastCompletedTimestamp", Date())
                    }
                }
            } else {
                val newHealth = (currentStats.health - 10).coerceIn(0, currentStats.maxHealth)
                transaction.update(userDoc, "health", newHealth)
            }
        }.await()
    }

    fun getPlayerStats(): Flow<PlayerStats?> {
        val userDoc = getUserDocument() ?: return emptyFlow()
        return userDoc.snapshots().map { snapshot ->
            if (!snapshot.exists()) {
                createInitialUserDataIfNeeded()
            }
            snapshot.toObject<PlayerStats>()
        }
    }

    suspend fun toggleFavoriteStatus(challengeId: String, isCurrentlyFavorite: Boolean) {
        getUserChallengesCollection()?.document(challengeId)?.update("isFavorite", !isCurrentlyFavorite)
    }

    suspend fun addChallenge(challenge: TinyWinChallenge) {
        getUserChallengesCollection()?.add(challenge)
    }

    suspend fun updateChallenge(challengeId: String, challenge: TinyWinChallenge) {
        getUserChallengesCollection()?.document(challengeId)?.set(challenge)
    }

    suspend fun deleteChallenge(challengeId: String) {
        getUserChallengesCollection()?.document(challengeId)?.delete()?.await()
    }

    suspend fun updateUserProfile(name: String, photoUrl: String?): Resource<Unit> {
        return try {
            val userDoc = getUserDocument() ?: throw Exception("Usuário não encontrado")
            val updates = mutableMapOf<String, Any>()
            updates["displayName"] = name
            if (photoUrl != null) {
                updates["photoUrl"] = photoUrl
            }
            userDoc.update(updates).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao atualizar perfil.")
        }
    }

    suspend fun uploadProfileImage(imageUri: Uri): Resource<Uri> {
        return try {
            val storageRef = getUserStorageRef() ?: throw Exception("Usuário não encontrado")
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro no upload da imagem.")
        }
    }

    suspend fun clearAllFavorites() {
        val challengesCollection = getUserChallengesCollection() ?: return
        val favoriteChallengesQuery = challengesCollection.whereEqualTo("isFavorite", true).get().await()
        val batch = db.batch()
        for (document in favoriteChallengesQuery.documents) {
            batch.update(document.reference, "isFavorite", false)
        }
        batch.commit().await()
    }

    private suspend fun deleteAllFromQuery(query: com.google.firebase.firestore.Query) {
        val batch = db.batch()
        val snapshot = query.get().await()
        snapshot.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    suspend fun deleteAllHabits() {
        getUserChallengesCollection()?.let {
            val query = it.whereEqualTo("type", TaskType.HABIT.name)
            deleteAllFromQuery(query)
        }
    }

    suspend fun deleteAllTodos() {
        getUserChallengesCollection()?.let {
            val query = it.whereEqualTo("type", TaskType.TODO.name)
            deleteAllFromQuery(query)
        }
    }

    private fun isSameDay(date1: Date?, date2: Date): Boolean {
        if (date1 == null) return false
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}