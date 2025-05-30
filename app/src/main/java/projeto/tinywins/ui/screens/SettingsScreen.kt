package projeto.tinywins.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme // Certifique-se que esta importação existe
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import projeto.tinywins.ui.theme.TinyWinsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController
    // TODO: Adicionar um callback para alterar o tema do app: onThemeChange: (Boolean) -> Unit
) {
    // CORREÇÃO APLICADA AQUI:
    val systemInitialThemeIsDark = isSystemInDarkTheme() // Chamada correta do Composable
    var isDarkModeEnabled by remember { mutableStateOf(systemInitialThemeIsDark) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
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
            Text(
                text = "Preferências de Tema",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Modo Escuro",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isDarkModeEnabled,
                    onCheckedChange = { newCheckedState ->
                        isDarkModeEnabled = newCheckedState
                        // TODO: Chamar onThemeChange(newCheckedState) para aplicar no app todo
                        println("Modo Escuro alterado para: $newCheckedState")
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Outras Configurações",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text("TODO: Botão Limpar Favoritos", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("TODO: Botão Redefinir Preferências", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Notificações",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = false, // TODO: Estado para notificações
                    onCheckedChange = { /* TODO: Lógica para notificações */ }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Settings Screen Light")
@Composable
fun SettingsScreenPreviewLight() {
    TinyWinsTheme(darkTheme = false) {
        val navController = rememberNavController()
        SettingsScreen(navController = navController)
    }
}

@Preview(showBackground = true, name = "Settings Screen Dark")
@Composable
fun SettingsScreenPreviewDark() {
    TinyWinsTheme(darkTheme = true) {
        val navController = rememberNavController()
        SettingsScreen(navController = navController)
    }
}