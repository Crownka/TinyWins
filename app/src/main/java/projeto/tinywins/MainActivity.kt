package projeto.tinywins

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text // Para os placeholders
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.NavArgs
import projeto.tinywins.ui.Screen
import projeto.tinywins.ui.screens.ChallengeDetailsScreen
import projeto.tinywins.ui.screens.FavoritesScreen
import projeto.tinywins.ui.screens.HomeScreen
import projeto.tinywins.ui.screens.SettingsScreen // Importe sua nova tela de Configurações
import projeto.tinywins.ui.theme.TinyWinsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TinyWinsTheme { // Seu tema Material 3
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route // Define a tela inicial
                ) {
                    // Destino para a HomeScreen
                    composable(route = Screen.Home.route) {
                        HomeScreen(
                            navController = navController,
                            challenges = sampleChallenges,
                            onChallengeClick = { challenge ->
                                navController.navigate(Screen.ChallengeDetails.createRoute(challenge.id))
                            }
                        )
                    }

                    // Rota para Detalhes do Desafio
                    composable(
                        route = Screen.ChallengeDetails.route, // "challenge_details_screen/{challengeId}"
                        arguments = listOf(navArgument(NavArgs.CHALLENGE_ID) { type = NavType.StringType })
                    ) { navBackStackEntry ->
                        val challengeId = navBackStackEntry.arguments?.getString(NavArgs.CHALLENGE_ID)
                        ChallengeDetailsScreen(
                            navController = navController,
                            challengeId = challengeId
                        )
                    }

                    // Rota para Favoritos
                    composable(route = Screen.Favorites.route) {
                        val currentlyFavoriteChallenges = sampleChallenges.filter { it.isFavorite }
                        FavoritesScreen(
                            navController = navController,
                            favoriteChallenges = currentlyFavoriteChallenges,
                            onChallengeClick = { challenge ->
                                navController.navigate(Screen.ChallengeDetails.createRoute(challenge.id))
                            }
                        )
                    }

                    // Rota para Configurações
                    composable(route = Screen.Settings.route) {
                        SettingsScreen(
                            navController = navController
                            // TODO: Passar o callback onThemeChange quando implementado
                        )
                    }

                    // Placeholders para as outras telas
                    composable(route = Screen.Help.route) {
                        // TODO: Substituir por HelpScreen(navController)
                        Text("Tela de Ajuda (Placeholder)")
                    }
                    composable(route = Screen.Profile.route) {
                        // TODO: Substituir por ProfileScreen(navController)
                        Text("Tela de Perfil (Placeholder)")
                    }
                }
            }
        }
    }
}