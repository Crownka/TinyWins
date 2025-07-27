package projeto.tinywins.data

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

// Lista de desafios mockados, agora mais enxuta para facilitar os testes.
val sampleChallenges = mutableStateListOf(
    // --- Hábitos ---
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Beber 2L de Água",
        description = "Mantenha-se hidratado bebendo pelo menos 8 copos (aprox. 2 litros) de água.",
        type = TaskType.HABIT,
        difficulty = Difficulty.EASY,
        resetFrequency = ResetFrequency.DAILY,
        isPositive = true,
        xp = 10,
        coins = 5,
        category = ChallengeCategory.SAUDE,
        isFavorite = true, // Deixei um favorito por padrão para testar a tela de favoritos
        quantifiable = true,
        currentProgress = 0,
        targetProgress = 8,
        pointsPerUnit = 1
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Ler por 15 minutos",
        description = "Dedique 15 minutos do seu dia para a leitura de um livro ou artigo.",
        type = TaskType.HABIT,
        difficulty = Difficulty.EASY,
        resetFrequency = ResetFrequency.DAILY,
        isPositive = true,
        xp = 20,
        coins = 10,
        category = ChallengeCategory.APRENDIZADO,
        isFavorite = false,
        quantifiable = true,
        currentProgress = 0,
        targetProgress = 15,
        pointsPerUnit = 1
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Arrumar a cama",
        description = "Comece o dia com uma pequena tarefa concluída.",
        type = TaskType.HABIT,
        difficulty = Difficulty.TRIVIAL,
        resetFrequency = ResetFrequency.DAILY,
        isPositive = true,
        xp = 5,
        coins = 5,
        category = ChallengeCategory.ORGANIZACAO,
        isFavorite = false,
        quantifiable = false
    ),
    // --- TODOs (Tarefas únicas) ---
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Planejar o dia seguinte",
        description = "Antes de dormir, liste as 3 principais tarefas para amanhã.",
        type = TaskType.TODO,
        difficulty = Difficulty.EASY,
        dueDate = null,
        xp = 25,
        coins = 10,
        category = ChallengeCategory.PRODUTIVIDADE,
        isFavorite = false,
        quantifiable = false
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Organizar a Mesa de Trabalho",
        description = "Um ambiente limpo ajuda na concentração. Organize sua mesa.",
        type = TaskType.TODO,
        difficulty = Difficulty.MEDIUM,
        dueDate = null,
        xp = 10,
        coins = 10,
        category = ChallengeCategory.ORGANIZACAO,
        isFavorite = true, // Mais um favorito para testes
        quantifiable = false
    )
)