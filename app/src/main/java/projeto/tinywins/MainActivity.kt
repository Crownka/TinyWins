package projeto.tinywins

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import projeto.tinywins.data.SettingsDataStore
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.NavArgs
import projeto.tinywins.ui.Screen
import projeto.tinywins.ui.screens.ChallengeDetailsScreen
import projeto.tinywins.ui.screens.FavoritesScreen
import projeto.tinywins.ui.screens.HomeScreen
import projeto.tinywins.ui.screens.SettingsScreen
import projeto.tinywins.ui.theme.TinyWinsTheme

class MainActivity : ComponentActivity() {
    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsDataStore = SettingsDataStore(applicationContext)

        setContent {
            val coroutineScope = rememberCoroutineScope()

            // Lendo as preferências salvas no DataStore como um 'State' do Compose
            val currentDarkTheme by settingsDataStore.themePreferenceFlow.collectAsState(initial = isSystemInDarkTheme())
            val areNotificationsEnabled by settingsDataStore.notificationsPreferenceFlow.collectAsState(initial = true)

            // O tema do App agora é controlado pelo valor que veio do DataStore
            TinyWinsTheme(useDarkTheme = currentDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    // Destino: HomeScreen
                    composable(Screen.Home.route) {
                        HomeScreen(
                            navController = navController,
                            challenges = sampleChallenges,
                            onChallengeClick = { challenge ->
                                navController.navigate(Screen.ChallengeDetails.createRoute(challenge.id))
                            }
                        )
                    }

                    // Destino: ChallengeDetailsScreen
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

                    // Destino: FavoritesScreen
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

                    // Destino: SettingsScreen (com todos os parâmetros necessários)
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            navController = navController,
                            currentThemeIsDark = currentDarkTheme,
                            onThemeToggled = { newThemeState ->
                                coroutineScope.launch {
                                    settingsDataStore.saveThemePreference(newThemeState)
                                }
                            },
                            areNotificationsEnabled = areNotificationsEnabled,
                            onNotificationsToggled = { isEnabled ->
                                coroutineScope.launch {
                                    settingsDataStore.saveNotificationsPreference(isEnabled)
                                }
                            }
                        )
                    }

                    // Destinos Placeholder
                    composable(Screen.Help.route) { Text("Tela de Ajuda (Placeholder)") }
                    composable(Screen.Profile.route) { Text("Tela de Perfil (Placeholder)") }
                }
            }
        }
    }
}