package projeto.tinywins.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.Screen
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
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (favoriteChallenges.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Você ainda não marcou nenhum desafio como favorito.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteChallenges) { challenge ->
                    ChallengeItemCard(
                        challenge = challenge,
                        onClick = { onChallengeClick(challenge) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Favorites Screen with Items")
@Composable
fun FavoritesScreenPreview() {
    TinyWinsTheme(useDarkTheme = true) {
        val navController = rememberNavController()
        val previewChallenges = sampleChallenges.take(3).map { it.copy(isFavorite = true) }
        FavoritesScreen(
            navController = navController,
            favoriteChallenges = previewChallenges,
            onChallengeClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Favorites Screen Empty")
@Composable
fun FavoritesScreenEmptyPreview() {
    TinyWinsTheme(useDarkTheme = true) {
        val navController = rememberNavController()
        FavoritesScreen(
            navController = navController,
            favoriteChallenges = emptyList(),
            onChallengeClick = {}
        )
    }
}