package projeto.tinywins

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
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
import projeto.tinywins.ui.theme.TinyWinsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TinyWinsTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(route = Screen.Home.route) {
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
                    ) { navBackStackEntry ->
                        val challengeId = navBackStackEntry.arguments?.getString(NavArgs.CHALLENGE_ID)
                        ChallengeDetailsScreen(
                            navController = navController,
                            challengeId = challengeId
                        )
                    }

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

                    composable(route = Screen.Settings.route) {
                        Text("Tela de Configurações (Placeholder)")
                    }
                    composable(route = Screen.Help.route) {
                        Text("Tela de Ajuda (Placeholder)")
                    }
                    composable(route = Screen.Profile.route) {
                        Text("Tela de Perfil (Placeholder)")
                    }
                }
            }
        }
    }
}