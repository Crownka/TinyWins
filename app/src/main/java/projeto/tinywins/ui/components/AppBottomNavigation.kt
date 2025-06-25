package projeto.tinywins.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import projeto.tinywins.ui.Screen

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    // Lista dos itens que vão aparecer na barra de navegação
    val items = listOf(
        Screen.Home,
        Screen.Favorites,
        Screen.Profile
    )

    NavigationBar {
        // Pego a rota atual para saber qual ícone deve ficar selecionado
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    // Defino o ícone para cada tela
                    val icon = when (screen) {
                        is Screen.Home -> Icons.Default.Home
                        is Screen.Favorites -> Icons.Default.Favorite
                        is Screen.Profile -> Icons.Default.Person
                        else -> Icons.Default.Home // Ícone padrão
                    }
                    Icon(icon, contentDescription = screen.route)
                },
                label = {
                    // Defino o texto para cada tela
                    val label = when (screen) {
                        is Screen.Home -> "Início"
                        is Screen.Favorites -> "Favoritos"
                        is Screen.Profile -> "Perfil"
                        else -> ""
                    }
                    Text(label)
                },
                // Verifico se o item atual é o que está selecionado
                selected = currentRoute == screen.route,
                onClick = {
                    // Ação de navegação para evitar empilhar a mesma tela várias vezes
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}