package projeto.tinywins.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // A tela continua recebendo a lista completa para observar mudanças
    challenges: List<TinyWinChallenge>,
    onChallengeClick: (TinyWinChallenge) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    // NOVO GATILHO: Crio um estado numérico para forçar a recomposição
    var listVersion by remember { mutableIntStateOf(0) }

    // A lista de favoritos agora depende do 'listVersion'.
    // Quando 'listVersion' mudar, esta lista será recalculada.
    val favoriteChallenges by remember(challenges, listVersion) {
        derivedStateOf {
            challenges.filter { it.isFavorite }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, "Opções")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Limpar Favoritos") },
                                onClick = {
                                    sampleChallenges.forEach { challenge ->
                                        if (challenge.isFavorite) {
                                            challenge.isFavorite = false
                                        }
                                    }
                                    // Incremento o gatilho para forçar a atualização da UI
                                    listVersion++
                                    menuExpanded = false
                                }
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
                items(
                    items = favoriteChallenges,
                    key = { challenge -> challenge.id }
                ) { challenge ->
                    AnimatedVisibility(
                        visible = true,
                        exit = shrinkVertically(animationSpec = tween(500)) +
                                fadeOut(animationSpec = tween(500))
                    ) {
                        ChallengeItemCard(
                            challenge = challenge,
                            onClick = { onChallengeClick(challenge) },
                            onPositiveAction = null,
                            onNegativeAction = null,
                            onTodoChecked = null,
                            onUnfavoriteClick = {
                                val itemToUnfavorite = sampleChallenges.find { it.id == challenge.id }
                                itemToUnfavorite?.isFavorite = false
                                // Incremento o gatilho para forçar a atualização
                                listVersion++
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
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
        FavoritesScreen(
            navController = navController,
            challenges = sampleChallenges,
            onChallengeClick = {}
        )
    }
}