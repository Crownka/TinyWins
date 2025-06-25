package projeto.tinywins.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.Screen
import projeto.tinywins.ui.components.CategoryBanner
import projeto.tinywins.ui.components.RelatedChallengeItem
import projeto.tinywins.ui.theme.TinyWinsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailsScreen(
    navController: NavHostController,
    challengeId: String?
) {
    val challenge = sampleChallenges.find { it.id == challengeId }
    var currentLocalIsFavorite by remember(challenge?.id) { mutableStateOf(challenge?.isFavorite ?: false) }

    LaunchedEffect(challenge?.isFavorite) {
        if (challenge != null) {
            currentLocalIsFavorite = challenge.isFavorite
        }
    }

    val relatedChallenges = remember(challenge?.id, challenge?.category) {
        if (challenge == null) emptyList()
        else sampleChallenges.filter { it.category == challenge.category && it.id != challenge.id }.take(5)
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
                    if (challenge != null) {
                        IconButton(onClick = {
                            challenge.isFavorite = !challenge.isFavorite
                            currentLocalIsFavorite = challenge.isFavorite
                        }) {
                            Icon(
                                imageVector = if (currentLocalIsFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favoritar",
                                tint = if (currentLocalIsFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (challenge != null) {
                CategoryBanner(category = challenge.category)

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Recompensas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
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
                        Text(
                            text = "Meu Progresso",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
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

                    if (relatedChallenges.isNotEmpty()) {
                        Text(
                            text = "Desafios Relacionados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(relatedChallenges) { relatedChallenge ->
                                RelatedChallengeItem(
                                    challenge = relatedChallenge,
                                    onClick = {
                                        navController.navigate(Screen.ChallengeDetails.createRoute(relatedChallenge.id))
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f, fill = false))
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("COMPLETAR", style = MaterialTheme.typography.labelLarge)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Desafio não encontrado.")
                }
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

@Preview(showBackground = true, name = "Details Screen Quantifiable")
@Composable
fun ChallengeDetailsScreenPreview() {
    TinyWinsTheme(useDarkTheme = false) {
        val navController = rememberNavController()
        ChallengeDetailsScreen(
            navController = navController,
            challengeId = sampleChallenges.first { it.quantifiable }.id
        )
    }
}