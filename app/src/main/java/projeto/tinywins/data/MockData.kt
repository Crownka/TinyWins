package projeto.tinywins.data

import java.util.UUID


val sampleChallenges = listOf(

    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Beber 8 copos d'água",
        description = "Mantenha-se hidratado bebendo pelo menos 8 copos (aprox. 2 litros) de água.",
        points = 10,
        category = ChallengeCategory.HEALTH,
        imageResId = null,
        quantifiable = true,
        currentProgress = 0,
        targetProgress = 8, // 8 copos
        pointsPerUnit = 1
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Ler por 15 minutos",
        description = "Dedique 15 minutos do seu dia para a leitura de um livro ou artigo.",
        points = 15,
        category = ChallengeCategory.LEARNING,
        imageResId = null,
        quantifiable = true,
        currentProgress = 0,
        targetProgress = 15, // 15 minutos
        pointsPerUnit = 1
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Caminhada de 10 minutos",
        description = "Faça uma pequena pausa para uma caminhada de 10 minutos.",
        points = 10,
        category = ChallengeCategory.HEALTH,
        imageResId = null,
        quantifiable = true,
        currentProgress = 0,
        targetProgress = 10 // 10 minutos
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Planejar o dia seguinte",
        description = "Antes de dormir, liste as 3 principais tarefas para amanhã.",
        points = 20,
        category = ChallengeCategory.PRODUCTIVITY,
        imageResId = null,
        quantifiable = false
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "10 minutos de Alongamento",
        description = "Faça uma pausa para se alongar e relaxar os músculos.",
        points = 10,
        category = ChallengeCategory.HEALTH,
        imageResId = null,
        quantifiable = true,
        currentProgress = 0,
        targetProgress = 10 // 10 minutos
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Desenhar por 10 minutos",
        description = "Solte sua criatividade desenhando o que vier à mente.",
        points = 15,
        category = ChallengeCategory.CREATIVITY,
        imageResId = null,
        quantifiable = true,
        currentProgress = 0,
        targetProgress = 5 // 5 minutos
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Meditar por 5 minutos",
        description = "Encontre um lugar calmo e concentre-se na sua respiração.",
        points = 15,
        category = ChallengeCategory.MINDFULNESS,
        imageResId = null,
        quantifiable = true,
        currentProgress = 0,
        targetProgress = 5 // 5 minutos
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Organizar a Mesa de Trabalho",
        description = "Um ambiente limpo ajuda na concentração. Organize sua mesa.",
        points = 10,
        category = ChallengeCategory.ORGANIZATION,
        imageResId = null,
        quantifiable = false
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Evitar refrigerante hoje",
        description = "Opte por bebidas mais saudáveis ao longo do dia.",
        points = 15,
        category = ChallengeCategory.HEALTH,
        imageResId = null,
        quantifiable = false
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Anotar gastos do dia",
        description = "Mantenha o controle financeiro anotando todas as suas despesas de hoje.",
        points = 10,
        category = ChallengeCategory.FINANCES,
        imageResId = null,
        quantifiable = false
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Fazer uma pausa sem telas",
        description = "Descanse os olhos e a mente por 10 minutos longe de telas.",
        points = 10,
        category = ChallengeCategory.MINDFULNESS,
        imageResId = null,
        quantifiable = true,
        currentProgress = 0,
        targetProgress = 10 // 10 minutos
    ),
    TinyWinChallenge(
        id = UUID.randomUUID().toString(),
        title = "Arrumar a cama pela manhã",
        description = "Comece o dia com uma pequena tarefa concluída.",
        points = 5,
        category = ChallengeCategory.ORGANIZATION,
        imageResId = null,
        quantifiable = false
    )
)