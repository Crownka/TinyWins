package projeto.tinywins.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import projeto.tinywins.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel,
    currentThemeIsDark: Boolean,
    onThemeToggled: (Boolean) -> Unit,
    areNotificationsEnabled: Boolean,
    onNotificationsToggled: (Boolean) -> Unit,
    areAnimationsEnabled: Boolean,
    onAnimationsToggled: (Boolean) -> Unit
) {
    var showClearFavoritesDialog by remember { mutableStateOf(false) }
    var showClearHabitsDialog by remember { mutableStateOf(false) }
    var showClearTodosDialog by remember { mutableStateOf(false) }

    if (showClearFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showClearFavoritesDialog = false },
            title = { Text("Confirmar Ação") },
            text = { Text("Você tem certeza que deseja remover todos os seus desafios favoritos? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllFavorites()
                        showClearFavoritesDialog = false
                    }
                ) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { showClearFavoritesDialog = false }) { Text("Cancelar") } }
        )
    }
    if (showClearHabitsDialog) {
        AlertDialog(
            onDismissRequest = { showClearHabitsDialog = false },
            title = { Text("Confirmar Ação") },
            text = { Text("Você tem certeza que deseja deletar TODOS os seus hábitos? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAllHabits()
                    showClearHabitsDialog = false
                }) { Text("Deletar Hábitos") }
            },
            dismissButton = { TextButton(onClick = { showClearHabitsDialog = false }) { Text("Cancelar") } }
        )
    }
    if (showClearTodosDialog) {
        AlertDialog(
            onDismissRequest = { showClearTodosDialog = false },
            title = { Text("Confirmar Ação") },
            text = { Text("Você tem certeza que deseja deletar TODAS as suas tarefas (To-Dos)? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAllTodos()
                    showClearTodosDialog = false
                }) { Text("Deletar To-Dos") }
            },
            dismissButton = { TextButton(onClick = { showClearTodosDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Preferências Gerais", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            SettingSwitchItem(
                title = "Modo Escuro",
                isChecked = currentThemeIsDark,
                onCheckedChange = onThemeToggled
            )
            SettingSwitchItem(
                title = "Notificações",
                isChecked = areNotificationsEnabled,
                onCheckedChange = onNotificationsToggled
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Gerenciamento de Dados", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Button(
                onClick = { showClearFavoritesDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) { Text("Limpar Todos os Favoritos") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showClearHabitsDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) { Text("Limpar Todos os Hábitos") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showClearTodosDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) { Text("Limpar Todos os To-Dos") }
        }
    }
}

@Composable
private fun SettingSwitchItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}