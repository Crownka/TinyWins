package projeto.tinywins

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import projeto.tinywins.ui.screens.SettingsScreen
import projeto.tinywins.ui.theme.TinyWinsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Inicializa com o tema do sistema.
            val systemInitialThemeIsDark = isSystemInDarkTheme()
            var currentDarkTheme by remember { mutableStateOf(systemInitialThemeIsDark) }

            TinyWinsTheme(useDarkTheme = currentDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            navController = navController,
                            challenges = sampleChallenges,
                            onChallengeClick = { challenge ->
                                navController.navigate(Screen.ChallengeDetails.createRoute(challenge.id))
                            }
                        )
                    }
                    composable(
                        route = Screen.ChallengeDetails.route,
                        arguments = listOf(navArgument(NavArgs.CHALLENGE_ID) { type = NavType.StringType })
                    ) { backStackEntry ->
                        val challengeId = backStackEntry.arguments?.getString(NavArgs.CHALLENGE_ID)
                        ChallengeDetailsScreen(
                            navController = navController,
                            challengeId = challengeId
                        )
                    }
                    composable(Screen.Favorites.route) {
                        val favorites = sampleChallenges.filter { it.isFavorite }
                        FavoritesScreen(
                            navController = navController,
                            favoriteChallenges = favorites,
                            onChallengeClick = { challenge ->
                                navController.navigate(Screen.ChallengeDetails.createRoute(challenge.id))
                            }
                        )
                    }
                    composable(Screen.Settings.route) {
                        // Passa o estado atual do tema e a função para alterá-lo.
                        SettingsScreen(
                            navController = navController,
                            currentThemeIsDark = currentDarkTheme,
                            onThemeToggled = { newThemeState ->
                                currentDarkTheme = newThemeState
                            }
                        )
                    }
                    composable(Screen.Help.route) { Text("Tela de Ajuda (Placeholder)") }
                    composable(Screen.Profile.route) { Text("Tela de Perfil (Placeholder)") }
                }
            }
        }
    }
}