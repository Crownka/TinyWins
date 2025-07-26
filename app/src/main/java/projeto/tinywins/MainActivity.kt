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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import projeto.tinywins.data.FirebaseRepository
import projeto.tinywins.data.NetworkStatusTracker
import projeto.tinywins.data.SettingsDataStore
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.NavArgs
import projeto.tinywins.ui.Screen
import projeto.tinywins.ui.auth.LoginScreen
import projeto.tinywins.ui.auth.RegistrationScreen
import projeto.tinywins.ui.components.ChallengeDetailsScreen
import projeto.tinywins.ui.screens.CreateTaskScreen
import projeto.tinywins.ui.screens.FavoritesScreen
import projeto.tinywins.ui.screens.HomeScreen
import projeto.tinywins.ui.screens.ProfileScreen
import projeto.tinywins.ui.screens.SettingsScreen
import projeto.tinywins.ui.theme.TinyWinsTheme
import projeto.tinywins.ui.viewmodel.CreateTaskViewModel
import projeto.tinywins.ui.viewmodel.HomeViewModel
import projeto.tinywins.ui.viewmodel.ViewModelFactory

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
            val areAnimationsEnabled by settingsDataStore.animationsPreferenceFlow.collectAsState(initial = true)

            val networkStatusTracker = remember { NetworkStatusTracker(applicationContext) }
            val repository = remember { FirebaseRepository(networkStatusTracker) }
            val viewModelFactory = remember { ViewModelFactory(repository) }

            TinyWinsTheme(useDarkTheme = currentDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.AuthCheck.route // NOVO PONTO DE PARTIDA
                ) {
                    val animationSpec = tween<Float>(durationMillis = 700)

                    composable(Screen.AuthCheck.route) {
                        // TODO: Lógica para verificar se o usuário está logado
                        // Por enquanto, só uma tela de loading que nos levará para o login
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        LaunchedEffect(Unit) {
                            // Simulando a verificação e navegando para o login
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.AuthCheck.route) { inclusive = true }
                            }
                        }
                    }

                    composable(Screen.Login.route) {
                        LoginScreen(navController = navController)
                    }

                    composable(Screen.Registration.route) {
                        RegistrationScreen(navController = navController)
                    }

                    composable(
                        route = Screen.Home.route,
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) }
                    ) {
                        val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                        HomeScreen(
                            navController = navController,
                            viewModel = homeViewModel,
                            onChallengeClick = { challenge ->
                                navController.navigate(Screen.ChallengeDetails.createRoute(challenge.id))
                            }
                        )
                    }

                    composable(
                        route = Screen.ChallengeDetails.route,
                        arguments = listOf(navArgument(NavArgs.CHALLENGE_ID) { type = NavType.StringType }),
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) }
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
                        exitTransition = { fadeOut(animationSpec) }
                    ) {
                        FavoritesScreen(
                            navController = navController,
                            challenges = sampleChallenges,
                            onChallengeClick = { challenge ->
                                navController.navigate(Screen.ChallengeDetails.createRoute(challenge.id))
                            }
                        )
                    }

                    composable(
                        route = Screen.Settings.route,
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) }
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
                        exitTransition = { fadeOut(animationSpec) }
                    ) {
                        val createTaskViewModel: CreateTaskViewModel = viewModel(factory = viewModelFactory)
                        CreateTaskScreen(
                            navController = navController,
                            viewModel = createTaskViewModel
                        )
                    }

                    composable(
                        route = Screen.Help.route,
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) }
                    ) {
                        // Implementar a tela de Ajuda aqui
                    }



                    composable(
                        route = Screen.Profile.route,
                        enterTransition = { fadeIn(animationSpec) },
                        exitTransition = { fadeOut(animationSpec) }
                    ) {
                        ProfileScreen(navController = navController)
                    }
                }
            }
        }
    }
}