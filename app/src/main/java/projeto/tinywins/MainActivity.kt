package projeto.tinywins

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import projeto.tinywins.ui.screens.ProfileScreen
import projeto.tinywins.ui.screens.SettingsScreen
import projeto.tinywins.ui.theme.TinyWinsTheme

class MainActivity : ComponentActivity() {
    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsDataStore = SettingsDataStore(applicationContext)

        setContent {
            val coroutineScope = rememberCoroutineScope()
            val currentDarkTheme by settingsDataStore.themePreferenceFlow.collectAsState(initial = isSystemInDarkTheme())
            val areNotificationsEnabled by settingsDataStore.notificationsPreferenceFlow.collectAsState(initial = true)

            TinyWinsTheme(useDarkTheme = currentDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    // Mudei a duração da animação aqui para 700ms
                    val fadeSpec = tween<Float>(durationMillis = 700)

                    composable(
                        route = Screen.Home.route,
                        enterTransition = { fadeIn(animationSpec = fadeSpec) },
                        exitTransition = { fadeOut(animationSpec = fadeSpec) },
                        popEnterTransition = { fadeIn(animationSpec = fadeSpec) },
                        popExitTransition = { fadeOut(animationSpec = fadeSpec) }
                    ) {
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
                        arguments = listOf(navArgument(NavArgs.CHALLENGE_ID) { type = NavType.StringType }),
                        enterTransition = { fadeIn(animationSpec = fadeSpec) },
                        exitTransition = { fadeOut(animationSpec = fadeSpec) },
                        popEnterTransition = { fadeIn(animationSpec = fadeSpec) },
                        popExitTransition = { fadeOut(animationSpec = fadeSpec) }
                    ) { backStackEntry ->
                        val challengeId = backStackEntry.arguments?.getString(NavArgs.CHALLENGE_ID)
                        ChallengeDetailsScreen(
                            navController = navController,
                            challengeId = challengeId
                        )
                    }

                    composable(
                        route = Screen.Favorites.route,
                        enterTransition = { fadeIn(animationSpec = fadeSpec) },
                        exitTransition = { fadeOut(animationSpec = fadeSpec) },
                        popEnterTransition = { fadeIn(animationSpec = fadeSpec) },
                        popExitTransition = { fadeOut(animationSpec = fadeSpec) }
                    ) {
                        val favorites = sampleChallenges.filter { it.isFavorite }
                        FavoritesScreen(
                            navController = navController,
                            favoriteChallenges = favorites,
                            onChallengeClick = { challenge ->
                                navController.navigate(Screen.ChallengeDetails.createRoute(challenge.id))
                            }
                        )
                    }

                    composable(
                        route = Screen.Settings.route,
                        enterTransition = { fadeIn(animationSpec = fadeSpec) },
                        exitTransition = { fadeOut(animationSpec = fadeSpec) },
                        popEnterTransition = { fadeIn(animationSpec = fadeSpec) },
                        popExitTransition = { fadeOut(animationSpec = fadeSpec) }
                    ) {
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

                    composable(
                        route = Screen.Help.route,
                        enterTransition = { fadeIn(animationSpec = fadeSpec) },
                        exitTransition = { fadeOut(animationSpec = fadeSpec) },
                        popEnterTransition = { fadeIn(animationSpec = fadeSpec) },
                        popExitTransition = { fadeOut(animationSpec = fadeSpec) }
                    ) {
                        Text("Tela de Ajuda (Placeholder)")
                    }

                    composable(
                        route = Screen.Profile.route,
                        enterTransition = { fadeIn(animationSpec = fadeSpec) },
                        exitTransition = { fadeOut(animationSpec = fadeSpec) },
                        popEnterTransition = { fadeIn(animationSpec = fadeSpec) },
                        popExitTransition = { fadeOut(animationSpec = fadeSpec) }
                    ) {
                        ProfileScreen(navController = navController)
                    }
                }
            }
        }
    }
}