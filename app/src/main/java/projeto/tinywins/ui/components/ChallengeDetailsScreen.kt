package projeto.tinywins.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import projeto.tinywins.data.Difficulty
import projeto.tinywins.data.ResetFrequency
import projeto.tinywins.data.TaskType
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.toColor
import projeto.tinywins.data.toIcon
import projeto.tinywins.ui.components.CategoryBanner
import projeto.tinywins.ui.viewmodel.ChallengeDetailsViewModel
import projeto.tinywins.ui.viewmodel.DetailsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailsScreen(
    navController: NavHostController,
    viewModel: ChallengeDetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Deletar Desafio") },
            text = { Text("Tem certeza que deseja deletar este desafio? Esta ação é permanente.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteChallenge()
                    showDeleteDialog = false
                    navController.popBackStack()
                }) { Text("Deletar") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Desafio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Deletar Desafio")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is DetailsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailsUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is DetailsUiState.Success -> {
                    ChallengeDetailsContent(
                        challenge = state.challenge,
                        onFavoriteClick = {
                            viewModel.toggleFavoriteStatus(state.challenge)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeDetailsContent(
    challenge: TinyWinChallenge,
    onFavoriteClick: () -> Unit
) {
    var isFavorite by remember(challenge.isFavorite) { mutableStateOf(challenge.isFavorite) }
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CategoryBanner(category = challenge.category)
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(text = challenge.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Recompensas Diárias", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = {
                        isFavorite = !isFavorite
                        onFavoriteClick()
                        coroutineScope.launch {
                            scale.animateTo(targetValue = 1.3f, animationSpec = tween(100))
                            scale.animateTo(targetValue = 1f, animationSpec = tween(100))
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritar",
                            modifier = Modifier.scale(scale.value),
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    RewardInfo(icon = Icons.Default.Star, text = "${challenge.xp} XP", iconColor = Color(0xFFFFC107))
                    RewardInfo(icon = Icons.Default.MonetizationOn, text = "${challenge.coins}", iconColor = Color(0xFFD4AF37))
                }
            }

            if (challenge.description.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (challenge.type == TaskType.HABIT && challenge.habitDurationInDays > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Progresso de Longo Prazo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    val habitProgress = (challenge.habitCurrentProgress.toFloat() / challenge.habitDurationInDays.toFloat()).coerceIn(0f, 1f)
                    val animatedHabitProgress by animateFloatAsState(targetValue = habitProgress, animationSpec = tween(500), label = "HabitProgressAnimation")

                    if (challenge.isHabitCompleted) {
                        InfoRow(label = "Status", value = "Hábito Concluído!")
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Progresso", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${challenge.habitCurrentProgress} / ${challenge.habitDurationInDays} dias",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { animatedHabitProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(MaterialTheme.shapes.small),
                            strokeCap = StrokeCap.Round
                        )
                    }

                    // SEÇÃO DE EXIBIÇÃO DO BÔNUS FINAL
                    if (!challenge.isHabitCompleted) {
                        val (bonusXp, bonusCoins, bonusDiamonds) = calculateCompletionBonus(challenge)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Recompensa Final",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    RewardInfo(icon = Icons.Default.Star, text = "$bonusXp XP", iconColor = MaterialTheme.colorScheme.onTertiaryContainer)
                                    RewardInfo(icon = Icons.Default.MonetizationOn, text = "$bonusCoins", iconColor = MaterialTheme.colorScheme.onTertiaryContainer)
                                    RewardInfo(icon = Icons.Default.Diamond, text = "$bonusDiamonds", iconColor = MaterialTheme.colorScheme.onTertiaryContainer)
                                }
                            }
                        }
                    }
                }
            }

            Column {
                Text(text = "Informações Adicionais", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (challenge.type == TaskType.HABIT && challenge.resetFrequency != null) {
                    InfoRow(label = "Frequência", value = challenge.resetFrequency.toPortuguese())
                }
                if (challenge.type == TaskType.TODO && challenge.dueDate != null) {
                    InfoRow(label = "Data de Entrega", value = formatDate(challenge.dueDate))
                }
                InfoRow(label = "Dificuldade", value = challenge.difficulty.toPortuguese())
            }
        }
    }
}

private fun calculateCompletionBonus(challenge: TinyWinChallenge): Triple<Int, Int, Int> {
    if (challenge.habitDurationInDays <= 0) return Triple(0, 0, 0)

    val difficultyMultiplier = (challenge.difficulty.ordinal + 1) * 1.5
    val frequencyMultiplier = when (challenge.resetFrequency) {
        ResetFrequency.WEEKLY -> 1.2
        ResetFrequency.MONTHLY -> 1.5
        else -> 1.0
    }
    val bonusXp = (challenge.habitDurationInDays * difficultyMultiplier * frequencyMultiplier * 2).toInt()
    val bonusCoins = (bonusXp / 2).coerceAtLeast(20)
    val bonusDiamonds = (challenge.habitDurationInDays / 10).coerceAtLeast(1)

    return Triple(bonusXp, bonusCoins, bonusDiamonds)
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RewardInfo(icon: ImageVector, text: String, iconColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = iconColor
        )
    }
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}