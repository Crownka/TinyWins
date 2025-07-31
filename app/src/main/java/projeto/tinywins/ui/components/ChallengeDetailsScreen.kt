package projeto.tinywins.ui.components

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import projeto.tinywins.data.TinyWinChallenge
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = challenge.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Recompensas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                if (challenge.diamonds > 0) {
                    RewardInfo(icon = Icons.Default.Diamond, text = "${challenge.diamonds}", iconColor = Color(0xFF4FC3F7))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
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
            if (challenge.quantifiable) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Meu Progresso", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val progress = (challenge.currentProgress.toFloat() / challenge.targetProgress.toFloat()).coerceIn(0f, 1f)
                        val animatedProgress by animateFloatAsState(targetValue = progress, label = "Progress Animation")
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.size(80.dp),
                                strokeWidth = 8.dp,
                            )
                            Text(
                                text = "${(animatedProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${challenge.currentProgress} / ${challenge.targetProgress}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.weight(1f, fill = false))
            Button(
                onClick = { /* TODO: Lógica para completar o desafio */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("COMPLETAR", style = MaterialTheme.typography.labelLarge)
            }
        }
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
            fontWeight = FontWeight.SemiBold
        )
    }
}