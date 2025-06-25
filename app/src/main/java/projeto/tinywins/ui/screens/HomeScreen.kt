package projeto.tinywins.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.Screen
import projeto.tinywins.ui.components.ChallengeItemCard
import projeto.tinywins.ui.components.PlayerStatusHeader
import projeto.tinywins.ui.theme.TinyWinsTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    challenges: List<TinyWinChallenge>,
    onChallengeClick: (TinyWinChallenge) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredChallenges by remember(searchQuery, challenges) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                challenges
            } else {
                val query = searchQuery.lowercase(Locale.getDefault())
                challenges.filter {
                    it.title.lowercase(Locale.getDefault()).contains(query) ||
                            it.description.lowercase(Locale.getDefault()).contains(query)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tiny Wins") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, "Mais opções")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(text = { Text("Favoritos") }, onClick = { navController.navigate(Screen.Favorites.route); menuExpanded = false })
                            DropdownMenuItem(text = { Text("Configurações") }, onClick = { navController.navigate(Screen.Settings.route); menuExpanded = false })
                            DropdownMenuItem(text = { Text("Ajuda") }, onClick = { navController.navigate(Screen.Help.route); menuExpanded = false })
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, "Início") },
                    label = { Text("Início") },
                    selected = navController.currentDestination?.route == Screen.Home.route,
                    onClick = {
                        if (navController.currentDestination?.route != Screen.Home.route) {
                            navController.navigate(Screen.Home.route) { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Favorite, "Favoritos") },
                    label = { Text("Favoritos") },
                    selected = navController.currentDestination?.route == Screen.Favorites.route,
                    onClick = { navController.navigate(Screen.Favorites.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, "Perfil") },
                    label = { Text("Perfil") },
                    selected = navController.currentDestination?.route == Screen.Profile.route,
                    onClick = { navController.navigate(Screen.Profile.route) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Desafio")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            PlayerStatusHeader()

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar desafios...") },
                leadingIcon = { Icon(Icons.Filled.Search, "Ícone de Busca") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            if (filteredChallenges.isEmpty() && searchQuery.isNotBlank()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhum desafio encontrado para \"$searchQuery\"", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredChallenges) { challenge ->
                        ChallengeItemCard(
                            challenge = challenge,
                            onClick = { onChallengeClick(challenge) },
                            onCompleteClick = {
                                println("Botão de completar clicado para: ${challenge.title}")
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TinyWinsTheme(useDarkTheme = true) {
        val navController = rememberNavController()
        HomeScreen(
            navController = navController,
            challenges = sampleChallenges,
            onChallengeClick = {}
        )
    }
}