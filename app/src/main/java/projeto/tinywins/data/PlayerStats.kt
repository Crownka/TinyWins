package projeto.tinywins.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PlayerStats(
    val userId: String = "",
    var displayName: String = "Jogador",
    var photoUrl: String = "",
    var health: Int = 50,
    var maxHealth: Int = 50,
    var xp: Int = 0,
    var level: Int = 1,
    var coins: Int = 0,
    var diamonds: Int = 0,
    @ServerTimestamp val lastUpdated: Date? = null
) {
    fun xpToNextLevel(): Int {
        return level * 100 + 50
    }
}