package projeto.tinywins.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import java.util.UUID

enum class TaskType { HABIT, TODO }

enum class Difficulty {
    TRIVIAL, EASY, MEDIUM, HARD;

    fun toPortuguese(): String {
        return when (this) {
            TRIVIAL -> "Trivial"
            EASY -> "Fácil"
            MEDIUM -> "Médio"
            HARD -> "Difícil"
        }
    }
}

enum class ResetFrequency {
    DAILY, WEEKLY, MONTHLY;

    fun toPortuguese(): String {
        return when (this) {
            DAILY -> "Diário"
            WEEKLY -> "Semanal"
            MONTHLY -> "Mensal"
        }
    }
}

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    @get:PropertyName("isCompleted") val isCompleted: Boolean = false
)

enum class ChallengeCategory {
    SAUDE, PRODUTIVIDADE, CRIATIVIDADE, APRENDIZADO,
    AUTOCONHECIMENTO, SOCIAL, FINANCAS, ORGANIZACAO
}

data class TinyWinChallenge(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: TaskType = TaskType.HABIT,
    val xp: Int = 0,
    val coins: Int = 0,
    val diamonds: Int = 0,
    val difficulty: Difficulty = Difficulty.EASY,
    @get:PropertyName("isPositive") val isPositive: Boolean = true,
    @get:PropertyName("isNegative") val isNegative: Boolean = false,
    val resetFrequency: ResetFrequency? = null,
    val dueDate: Long? = null,
    val reminders: List<Long> = emptyList(),
    val checklist: List<ChecklistItem> = emptyList(),
    val category: ChallengeCategory = ChallengeCategory.PRODUTIVIDADE,
    @get:PropertyName("isCompleted") val isCompleted: Boolean = false,
    @get:PropertyName("isFavorite") val isFavorite: Boolean = false,
    val quantifiable: Boolean = false,
    var currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val pointsPerUnit: Int = 0,
    @ServerTimestamp val createdAt: Date? = null
)

@Composable
fun ChallengeCategory.toColor(): Color {
    return when (this) {
        ChallengeCategory.SAUDE -> Color(0xFF4CAF50)
        ChallengeCategory.PRODUTIVIDADE -> Color(0xFF2196F3)
        ChallengeCategory.CRIATIVIDADE -> Color(0xFFFFC107)
        ChallengeCategory.APRENDIZADO -> Color(0xFF9C27B0)
        ChallengeCategory.AUTOCONHECIMENTO -> Color(0xFF795548)
        ChallengeCategory.SOCIAL -> Color(0xFFE91E63)
        ChallengeCategory.FINANCAS -> Color(0xFF009688)
        ChallengeCategory.ORGANIZACAO -> Color(0xFF607D8B)
    }
}

val ChallengeCategory.toIcon: ImageVector
    get() = when (this) {
        ChallengeCategory.SAUDE -> Icons.Default.Favorite
        ChallengeCategory.PRODUTIVIDADE -> Icons.Default.Bolt
        ChallengeCategory.CRIATIVIDADE -> Icons.Default.Brush
        ChallengeCategory.APRENDIZADO -> Icons.AutoMirrored.Filled.MenuBook
        ChallengeCategory.AUTOCONHECIMENTO -> Icons.Default.SelfImprovement
        ChallengeCategory.SOCIAL -> Icons.Default.Group
        ChallengeCategory.FINANCAS -> Icons.Default.MonetizationOn
        ChallengeCategory.ORGANIZACAO -> Icons.Default.Checklist
    }