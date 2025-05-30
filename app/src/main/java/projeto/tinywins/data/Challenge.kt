package projeto.tinywins.data

import androidx.annotation.DrawableRes

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
    var points: Int,
    val category: ChallengeCategory,
    @DrawableRes val imageResId: Int?,
    var isCompleted: Boolean = false,
    var isFavorite: Boolean = false,

    val quantifiable: Boolean = false,
    var currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val pointsPerUnit: Int = 0
)