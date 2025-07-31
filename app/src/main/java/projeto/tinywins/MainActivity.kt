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
import projeto.tinywins.data.auth.AuthRepository
import projeto.tinywins.ui.NavArgs
import projeto.tinywins.ui.Screen
import projeto.tinywins.ui.auth.LoginScreen
import projeto.tinywins.ui.auth.RegistrationScreen
import projeto.tinywins.ui.components.ChallengeDetailsScreen
import projeto.tinywins.ui.screens.CreateTaskScreen
import projeto.tinywins.ui.screens.FavoritesScreen
import projeto.tinywins.ui.screens.HelpScreen
import projeto.tinywins.ui.screens.HomeScreen
import projeto.tinywins.ui.screens.ProfileScreen
import projeto.tinywins.ui.screens.SettingsScreen
import projeto.tinywins.ui.theme.TinyWinsTheme
import projeto.tinywins.ui.viewmodel.ChallengeDetailsViewModel
import projeto.tinywins.ui.viewmodel.CreateTaskViewModel
import projeto.tinywins.ui.viewmodel.FavoritesViewModel
import projeto.tinywins.ui.viewmodel.HomeViewModel
import projeto.tinywins.ui.viewmodel.LoginViewModel
import projeto.tinywins.ui.viewmodel.ProfileViewModel
import projeto.tinywins.ui.viewmodel.RegistrationViewModel
import projeto.tinywins.ui.viewmodel.SettingsViewModel
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

            val networkStatusTracker = remember { NetworkStatusTracker(applicationContext) }
            val firebaseRepository = remember { FirebaseRepository(networkStatusTracker) }
            val authRepository = remember { AuthRepository() }
            val viewModelFactory = remember { ViewModelFactory(firebaseRepository, authRepository) }

            TinyWinsTheme(useDarkTheme = currentDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.AuthCheck.route
                ) {
                    composable(Screen.AuthCheck.route) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        LaunchedEffect(Unit) {
                            if (authRepository.currentUser != null) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.AuthCheck.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.AuthCheck.route) { inclusive = true }
                                }
                            }
                        }
                    }

                    composable(Screen.Login.route) {
                        val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory)
                        LoginScreen(
                            navController = navController,
                            viewModel = loginViewModel
                        )
                    }

                    composable(Screen.Registration.route) {
                        val registrationViewModel: RegistrationViewModel = viewModel(factory = viewModelFactory)
                        RegistrationScreen(
                            navController = navController,
                            viewModel = registrationViewModel
                        )
                    }

                    composable(route = Screen.Home.route) {
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
                        arguments = listOf(navArgument(NavArgs.CHALLENGE_ID) { type = NavType.StringType })
                    ) { backStackEntry ->
                        val challengeId = backStackEntry.arguments?.getString(NavArgs.CHALLENGE_ID)
                        val detailsViewModel = ChallengeDetailsViewModel(
                            challengeId = challengeId ?: "",
                            repository = firebaseRepository
                        )
                        ChallengeDetailsScreen(
                            navController = navController,
                            viewModel = detailsViewModel
                        )
                    }

                    composable(
                        route = Screen.CreateTask.route,
                        arguments = listOf(navArgument(NavArgs.TASK_TYPE) {
                            type = NavType.StringType
                            nullable = true
                        })
                    ) { backStackEntry ->
                        val taskType = backStackEntry.arguments?.getString(NavArgs.TASK_TYPE)
                        val createTaskViewModel: CreateTaskViewModel = viewModel(factory = viewModelFactory)
                        CreateTaskScreen(
                            navController = navController,
                            viewModel = createTaskViewModel,
                            startingTaskType = taskType
                        )
                    }

                    composable(route = Screen.Favorites.route) {
                        val favoritesViewModel: FavoritesViewModel = viewModel(factory = viewModelFactory)
                        FavoritesScreen(
                            navController = navController,
                            viewModel = favoritesViewModel,
                            onChallengeClick = { challenge ->
                                navController.navigate(Screen.ChallengeDetails.createRoute(challenge.id))
                            }
                        )
                    }

                    composable(route = Screen.Settings.route) {
                        val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
                        val areNotificationsEnabled by settingsDataStore.notificationsPreferenceFlow.collectAsState(initial = true)
                        val areAnimationsEnabled by settingsDataStore.animationsPreferenceFlow.collectAsState(initial = true)
                        SettingsScreen(
                            navController = navController,
                            viewModel = settingsViewModel,
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

                    composable(route = Screen.Help.route) {
                        HelpScreen(navController = navController)
                    }

                    composable(route = Screen.Profile.route) {
                        val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
                        ProfileScreen(
                            navController = navController,
                            viewModel = profileViewModel
                        )
                    }
                }
            }
        }
    }
}