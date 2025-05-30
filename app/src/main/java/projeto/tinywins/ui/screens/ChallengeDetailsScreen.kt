package projeto.tinywins.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.theme.TinyWinsTheme
import projeto.tinywins.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailsScreen(
    navController: NavHostController,
    challengeId: String?
) {
    val challenge = sampleChallenges.find { it.id == challengeId }

    var isFavoriteState by remember(challenge?.id) {
        mutableStateOf(challenge?.isFavorite ?: false)
    }


    val relatedChallenges = remember(challenge?.id, challenge?.category) {
        if (challenge == null) {
            emptyList()
        } else {
            sampleChallenges.filter {
                it.category == challenge.category && it.id != challenge.id
            }.take(5)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(challenge?.title ?: "Detalhes do Desafio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    if (challenge != null) {
                        IconButton(onClick = {
                            isFavoriteState = !isFavoriteState
                            println("Desafio '${challenge.title}' marcado como favorito: $isFavoriteState")
                        }) {
                            Icon(
                                imageVector = if (isFavoriteState) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isFavoriteState) "Desmarcar como favorito" else "Marcar como favorito",
                                tint = if (isFavoriteState) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (challenge != null) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Categoria: ${challenge.category.name.toLowerCase().capitalize()}",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pontos: ${challenge.points}",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = challenge.description,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))


                if (relatedChallenges.isNotEmpty()) {
                    Text(
                        text = "Desafios Relacionados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(relatedChallenges) { relatedChallenge ->
                            RelatedChallengeItem(
                                challenge = relatedChallenge,
                                onClick = {
                                    navController.navigate(Screen.ChallengeDetails.createRoute(relatedChallenge.id)) {
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }


                Text("TODO: Botões de Ação adicionais", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("TODO: Player Multimídia (Opcional)", style = MaterialTheme.typography.labelMedium)


            } else {
                Text("Desafio não encontrado.", modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChallengeDetailsScreenPreview() {
    TinyWinsTheme {
        val navController = rememberNavController()
        val previewChallenge = sampleChallenges.firstOrNull()?.apply { isFavorite = false }
        ChallengeDetailsScreen(
            navController = navController,
            challengeId = previewChallenge?.id
        )
    }
}