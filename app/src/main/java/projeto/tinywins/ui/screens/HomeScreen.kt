package projeto.tinywins.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import projeto.tinywins.ui.Screen
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
        topBar = { // Conteúdo da TopAppBar restaurado aqui
            TopAppBar(
                title = { Text("Tiny Wins") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer // Garante cor dos ícones de ação
                ),
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Mais opções"
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Favoritos") },
                                onClick = {
                                    navController.navigate(Screen.Favorites.route)
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Configurações") },
                                onClick = {
                                    navController.navigate(Screen.Settings.route)
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Ajuda") },
                                onClick = {
                                    navController.navigate(Screen.Help.route)
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = { // Conteúdo da NavigationBar restaurado aqui
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Início") },
                    label = { Text("Início") },
                    selected = navController.currentDestination?.route == Screen.Home.route,
                    onClick = {
                        if (navController.currentDestination?.route != Screen.Home.route) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") },
                    selected = navController.currentDestination?.route == Screen.Favorites.route,
                    onClick = { navController.navigate(Screen.Favorites.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = navController.currentDestination?.route == Screen.Profile.route,
                    onClick = { navController.navigate(Screen.Profile.route) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // innerPadding do Scaffold é aplicado aqui
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar desafios...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Ícone de Busca") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            if (filteredChallenges.isEmpty() && searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Nenhum desafio encontrado para \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredChallenges) { challenge ->
                        ChallengeItemCard(
                            challenge = challenge,
                            onClick = { onChallengeClick(challenge) }
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
    TinyWinsTheme {
        val navController = rememberNavController()
        HomeScreen(
            navController = navController,
            challenges = sampleChallenges,
            onChallengeClick = {}
        )
    }
}