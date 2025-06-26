package projeto.tinywins.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.components.ChallengeItemCard
import projeto.tinywins.ui.theme.TinyWinsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavHostController,
    favoriteChallenges: List<TinyWinChallenge>,
    onChallengeClick: (TinyWinChallenge) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Favoritos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (favoriteChallenges.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Você ainda não marcou nenhum desafio como favorito.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteChallenges) { challenge ->
                    ChallengeItemCard(
                        challenge = challenge,
                        onClick = { onChallengeClick(challenge) },
                        onPositiveAction = { println("Ação POSITIVA para favorito: ${challenge.title}") },
                        onNegativeAction = { println("Ação NEGATIVA para favorito: ${challenge.title}") },
                        onTodoChecked = { isChecked ->
                            challenge.isCompleted = isChecked
                            println("TODO favorito '${challenge.title}' marcado como: $isChecked")
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Favorites Screen with Items")
@Composable
fun FavoritesScreenPreview() {
    TinyWinsTheme(useDarkTheme = false) {
        val navController = rememberNavController()
        val previewChallenges = sampleChallenges.take(3).map { it.copy(isFavorite = true) }
        FavoritesScreen(
            navController = navController,
            favoriteChallenges = previewChallenges,
            onChallengeClick = {}
        )
    }
}