package projeto.tinywins.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import projeto.tinywins.data.TaskType
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.Screen
import projeto.tinywins.ui.components.ChallengeItemCard
import projeto.tinywins.ui.components.PlayerStatusHeader
import projeto.tinywins.ui.theme.TinyWinsTheme
import java.util.Locale

// A forma de losango
val DiamondShape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
    close()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    challenges: List<TinyWinChallenge>,
    onChallengeClick: (TinyWinChallenge) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(TaskType.HABIT) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        delay(1500) // Diminuí um pouco o tempo de carregamento
        isLoading = false
    }

    val filteredChallenges by remember(searchQuery, challenges, selectedTab) {
        derivedStateOf {
            val tasksOfType = challenges.filter { it.type == selectedTab }
            if (searchQuery.isBlank()) {
                tasksOfType
            } else {
                val query = searchQuery.lowercase(Locale.getDefault())
                tasksOfType.filter {
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
                        IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Filled.MoreVert, "Mais opções") }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(text = { Text("Favoritos") }, onClick = { navController.navigate(Screen.Favorites.route); menuExpanded = false })
                            DropdownMenuItem(text = { Text("Configurações") }, onClick = { navController.navigate(Screen.Settings.route); menuExpanded = false })
                            DropdownMenuItem(text = { Text("Ajuda") }, onClick = { navController.navigate(Screen.Help.route); menuExpanded = false })
                        }
                    }
                }
            )
        },
        bottomBar = {
            // A NOVA BARRA INFERIOR COM O BOTÃO '+' INTEGRADO
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item de Navegação para Hábitos
                    BottomTabItem(
                        icon = Icons.Default.Sync,
                        label = "Hábitos",
                        isSelected = selectedTab == TaskType.HABIT,
                        onClick = { selectedTab = TaskType.HABIT }
                    )

                    // O Botão de Adicionar agora é um item customizado no meio da barra
                    Surface(
                        shape = DiamondShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { navController.navigate(Screen.CreateTask.route) },
                        tonalElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar Tarefa",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    // Item de Navegação para To Do's
                    BottomTabItem(
                        icon = Icons.Default.Checklist,
                        label = "To Do's",
                        isSelected = selectedTab == TaskType.TODO,
                        onClick = { selectedTab = TaskType.TODO }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                PlayerStatusHeader(onClick = { navController.navigate(Screen.Profile.route) })

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar em ${selectedTab.name.lowercase().replaceFirstChar { it.titlecase() }}...") },
                    leadingIcon = { Icon(Icons.Filled.Search, "Ícone de Busca") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                onPositiveAction = { println("Ação POSITIVA para: ${challenge.title}") },
                                onNegativeAction = { println("Ação NEGATIVA para: ${challenge.title}") },
                                onTodoChecked = { isChecked ->
                                    challenge.isCompleted = isChecked
                                    println("TODO '${challenge.title}' marcado como: $isChecked")
                                },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        Icon(imageVector = icon, contentDescription = label, tint = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal)
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