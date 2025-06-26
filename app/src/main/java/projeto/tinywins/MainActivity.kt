package projeto.tinywins

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
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
import projeto.tinywins.ui.screens.CreateTaskScreen
import projeto.tinywins.ui.screens.FavoritesScreen
import projeto.tinywins.ui.screens.HomeScreen
import projeto.tinywins.ui.screens.ProfileScreen
import projeto.tinywins.ui.screens.SettingsScreen
import projeto.tinywins.ui.theme.TinyWinsTheme

class MainActivity : ComponentActivity() {
    private lateinit var settingsDataStore: SettingsDataStore

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("Permissão para notificações CONCEDIDA")
        } else {
            println("Permissão para notificações NEGADA")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsDataStore = SettingsDataStore(applicationContext)

        setContent {
            LaunchedEffect(key1 = true) {
                askNotificationPermission()
            }

            val coroutineScope = rememberCoroutineScope()
            val currentDarkTheme by settingsDataStore.themePreferenceFlow.collectAsState(initial = isSystemInDarkTheme())
            val areNotificationsEnabled by settingsDataStore.notificationsPreferenceFlow.collectAsState(initial = true)
            // A preferência de animação ainda é lida, mas não está sendo usada para desativá-las
            val areAnimationsEnabled by settingsDataStore.animationsPreferenceFlow.collectAsState(initial = true)

            TinyWinsTheme(useDarkTheme = currentDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    val animationSpec = tween<Float>(durationMillis = 700)

                    composable(
                        route = Screen.Home.route,
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) },
                        popEnterTransition = { fadeIn(animationSpec) },
                        popExitTransition = { fadeOut(animationSpec) }
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
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) },
                        popEnterTransition = { fadeIn(animationSpec) },
                        popExitTransition = { fadeOut(animationSpec) }
                    ) { backStackEntry ->
                        val challengeId = backStackEntry.arguments?.getString(NavArgs.CHALLENGE_ID)
                        ChallengeDetailsScreen(
                            navController = navController,
                            challengeId = challengeId
                        )
                    }

                    composable(
                        route = Screen.Favorites.route,
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) },
                        popEnterTransition = { fadeIn(animationSpec) },
                        popExitTransition = { fadeOut(animationSpec) }
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
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) },
                        popEnterTransition = { fadeIn(animationSpec) },
                        popExitTransition = { fadeOut(animationSpec) }
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
                            },
                            // O switch de animação será removido, então este callback não é mais necessário
                            areAnimationsEnabled = areAnimationsEnabled,
                            onAnimationsToggled = { isEnabled ->
                                coroutineScope.launch {
                                    settingsDataStore.saveAnimationsPreference(isEnabled)
                                }
                            }
                        )
                    }

                    composable(
                        route = Screen.CreateTask.route,
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) },
                        popEnterTransition = { fadeIn(animationSpec) },
                        popExitTransition = { fadeOut(animationSpec) }
                    ) {
                        CreateTaskScreen(navController = navController)
                    }

                    composable(
                        route = Screen.Help.route,
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) },
                        popEnterTransition = { fadeIn(animationSpec) },
                        popExitTransition = { fadeOut(animationSpec) }
                    ) {
                        // Text("Tela de Ajuda (Placeholder)")
                    }

                    composable(
                        route = Screen.Profile.route,
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) },
                        popEnterTransition = { fadeIn(animationSpec) },
                        popExitTransition = { fadeOut(animationSpec) }
                    ) {
                        ProfileScreen(navController = navController)
                    }
                }
            }
        }
    }
}