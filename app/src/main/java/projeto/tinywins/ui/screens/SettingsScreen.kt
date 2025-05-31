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
    navController: NavHostController,
    currentThemeIsDark: Boolean,      // Recebe o estado atual do tema
    onThemeToggled: (Boolean) -> Unit // Função para avisar que o tema mudou
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Botão para voltar
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
            Text("Preferências de Tema", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

            // Linha para a opção de Modo Escuro
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo Escuro", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = currentThemeIsDark, // O estado do Switch vem do parâmetro
                    onCheckedChange = { newCheckedState ->
                        onThemeToggled(newCheckedState)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Outras Configurações", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            // TODO: F4 - Adicionar botões reais aqui
            Text("TODO: Botão Limpar Favoritos", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("TODO: Botão Redefinir Preferências", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
            // Linha para a opção de Notificações
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Notificações", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = false, // TODO: F4 - Estado e lógica para notificações
                    onCheckedChange = { /* TODO */ }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Settings Screen Light")
@Composable
fun SettingsScreenPreviewLight() {
    TinyWinsTheme(useDarkTheme = false) {
        val navController = rememberNavController()
        SettingsScreen(
            navController = navController,
            currentThemeIsDark = false,
            onThemeToggled = {}
        )
    }
}

@Preview(showBackground = true, name = "Settings Screen Dark")
@Composable
fun SettingsScreenPreviewDark() {
    TinyWinsTheme(useDarkTheme = true) {
        val navController = rememberNavController()
        SettingsScreen(
            navController = navController,
            currentThemeIsDark = true,
            onThemeToggled = {}
        )
    }
}