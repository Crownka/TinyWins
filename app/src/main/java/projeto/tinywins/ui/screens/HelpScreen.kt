package projeto.tinywins.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajuda & FAQ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FaqItem(
                question = "O que é o Tiny Wins?",
                answer = "Tiny Wins é um aplicativo de produtividade que transforma a criação de hábitos e a conclusão de tarefas em um jogo. Ao completar seus desafios, você ganha XP, moedas e sobe de nível, como em um RPG."
            )
            FaqItem(
                question = "Qual a diferença entre Hábito e To Do?",
                answer = "Um 'Hábito' é uma tarefa recorrente que você pode completar várias vezes (ex: diariamente). Um 'To Do' é uma tarefa única com um objetivo específico. Hábitos podem ser positivos (dão recompensas), negativos (tiram vida) ou ambos. To-Dos são sempre positivos."
            )
            FaqItem(
                question = "Como funciona o sistema de XP e Níveis?",
                answer = "Cada desafio completado te dá pontos de experiência (XP). Ao acumular XP suficiente, você sobe de nível. Subir de nível restaura sua saúde completamente e te dá diamantes como recompensa!"
            )
            FaqItem(
                question = "Para que serve a Saúde?",
                answer = "Sua saúde diminui quando você falha em completar um hábito negativo (clicando no botão '-'). Se sua saúde chegar a zero, você sofrerá uma penalidade. Completar hábitos positivos recupera um pouco de sua saúde."
            )
            FaqItem(
                question = "Como funcionam as notificações?",
                answer = "Ao criar ou editar uma tarefa do tipo 'To Do', você pode adicionar lembretes em horários específicos. O aplicativo irá te enviar uma notificação para que você não se esqueça de completar suas metas."
            )
            FaqItem(
                question = "Como funciona o modo offline?",
                answer = "O aplicativo salva seus dados localmente. Você pode criar, editar e completar desafios mesmo sem internet. Assim que a conexão for restabelecida, todas as suas alterações serão sincronizadas automaticamente com a nuvem."
            )
            FaqItem(
                question = "Meus dados estão seguros?",
                answer = "Sim. Cada usuário possui sua própria base de dados na nuvem, protegida pela autenticação do Firebase. Ninguém além de você pode acessar seus desafios e seu progresso."
            )
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}