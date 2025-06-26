package projeto.tinywins.data

import androidx.annotation.DrawableRes
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
import java.util.UUID

enum class TaskType {
    HABIT,
    TODO
}

enum class Difficulty {
    TRIVIAL,
    EASY,
    MEDIUM,
    HARD
}

enum class ResetFrequency {
    DAILY,
    WEEKLY,
    MONTHLY
}

// NOVA DATA CLASS para um item da checklist
data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    var text: String,
    var isCompleted: Boolean = false
)

enum class ChallengeCategory {
    SAUDE,
    PRODUTIVIDADE,
    CRIATIVIDADE,
    APRENDIZADO,
    AUTOCONHECIMENTO,
    SOCIAL,
    FINANCAS,
    ORGANIZACAO
}

data class TinyWinChallenge(
    val id: String,
    val title: String,
    val description: String,
    val type: TaskType,
    val xp: Int,
    val coins: Int,
    val diamonds: Int = 0,
    val difficulty: Difficulty = Difficulty.EASY,
    val isPositive: Boolean = true,
    val isNegative: Boolean = false,
    val resetFrequency: ResetFrequency? = ResetFrequency.DAILY,
    val dueDate: Long? = null,
    val reminders: List<Long> = emptyList(),

    // NOVO CAMPO para a lista de subtarefas
    val checklist: List<ChecklistItem> = emptyList(),

    val category: ChallengeCategory,
    @DrawableRes val imageResId: Int?,
    var isCompleted: Boolean = false,
    var isFavorite: Boolean = false,
    val quantifiable: Boolean = false,
    var currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val pointsPerUnit: Int = 0
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