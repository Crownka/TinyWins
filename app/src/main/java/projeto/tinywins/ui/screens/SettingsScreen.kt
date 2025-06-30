package projeto.tinywins.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.theme.TinyWinsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    currentThemeIsDark: Boolean,
    onThemeToggled: (Boolean) -> Unit,
    areNotificationsEnabled: Boolean,
    onNotificationsToggled: (Boolean) -> Unit,
    areAnimationsEnabled: Boolean,
    onAnimationsToggled: (Boolean) -> Unit
) {
    // Estado para controlar a visibilidade do diálogo de confirmação
    var showClearFavoritesDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmação para limpar os favoritos
    if (showClearFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showClearFavoritesDialog = false },
            title = { Text("Confirmar Ação") },
            text = { Text("Você tem certeza que deseja remover todos os seus desafios favoritos? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Lógica para limpar os favoritos
                        sampleChallenges.forEach { challenge ->
                            if (challenge.isFavorite) {
                                challenge.isFavorite = false
                            }
                        }
                        showClearFavoritesDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearFavoritesDialog = false }) {
                    Text("Cancelar")
                }
            }
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
            // O switch de animações foi removido da UI, mas a lógica permanece no MainActivity
            // para manter as animações sempre ativas (por enquanto)

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Gerenciamento de Dados", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

            // BOTÃO "LIMPAR FAVORITOS"
            Button(
                onClick = { showClearFavoritesDialog = true }, // Abre o diálogo
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Limpar Todos os Favoritos")
            }
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

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreviewLight() {
    TinyWinsTheme(useDarkTheme = false) {
        val navController = rememberNavController()
        SettingsScreen(
            navController = navController,
            currentThemeIsDark = false,
            onThemeToggled = {},
            areNotificationsEnabled = true,
            onNotificationsToggled = {},
            areAnimationsEnabled = true,
            onAnimationsToggled = {}
        )
    }
}