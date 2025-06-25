package projeto.tinywins.ui

object NavArgs {
    const val CHALLENGE_ID = "challengeId"
}

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object ChallengeDetails : Screen("challenge_details_screen/{${NavArgs.CHALLENGE_ID}}") {
        fun createRoute(challengeId: String) = "challenge_details_screen/$challengeId"
    }
    data object Favorites : Screen("favorites_screen")
    data object Settings : Screen("settings_screen")
    data object Help : Screen("help_screen")
    data object Profile : Screen("profile_screen")
    data object CreateTask : Screen("create_task_screen") // NOVA ROTA
}