package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Biometric : Screen("biometric")
    data object Syncing : Screen("syncing")

    data object Home : Screen("home")
    data object Schedule : Screen("schedule")
    data object Venues : Screen("venues")
    data object Notifications : Screen("notifications")
    data object Profile : Screen("profile")
}
