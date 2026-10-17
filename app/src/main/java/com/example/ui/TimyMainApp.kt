package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.datastore.UserPreferences
import com.example.ui.navigation.Screen
import com.example.ui.screens.auth.BiometricScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.SyncingScreen
import com.example.ui.screens.auth.WelcomeScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.schedule.ScheduleScreen
import com.example.ui.screens.venues.VenuesScreen
import com.example.ui.theme.TimyCrimson
import com.example.ui.theme.XBlue
import com.example.ui.viewmodels.AuthEffect
import com.example.ui.viewmodels.AuthViewModel
import com.example.ui.viewmodels.DashboardViewModel
import com.example.ui.viewmodels.NotificationsViewModel
import com.example.ui.viewmodels.ProfileViewModel
import com.example.ui.viewmodels.ScheduleViewModel
import com.example.ui.viewmodels.VenuesViewModel
import com.example.ui.viewmodels.ViewModelFactory
import kotlinx.coroutines.launch

private data class NavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun TimyMainApp(
    userPreferences: UserPreferences,
    currentThemeMode: com.example.ui.theme.AppThemeMode = com.example.ui.theme.AppThemeMode.LIGHTS_OUT,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val factory = remember { ViewModelFactory.create(context) }

    val isOnboarded by userPreferences.isOnboarded.collectAsState(initial = false)
    val isDarkTheme = currentThemeMode != com.example.ui.theme.AppThemeMode.LIGHT

    val toggleTheme: () -> Unit = {
        scope.launch {
            if (isDarkTheme) {
                userPreferences.setThemeMode(com.example.ui.theme.AppThemeMode.LIGHT.name)
            } else {
                userPreferences.setThemeMode(com.example.ui.theme.AppThemeMode.LIGHTS_OUT.name)
            }
        }
    }

    var currentScreen by remember {
        mutableStateOf<Screen>(if (isOnboarded) Screen.Home else Screen.Welcome)
    }

    LaunchedEffect(isOnboarded) {
        if (!isOnboarded && currentScreen != Screen.Welcome && currentScreen != Screen.Login &&
            currentScreen != Screen.Biometric && currentScreen != Screen.Syncing) {
            currentScreen = Screen.Welcome
        }
    }

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
    val scheduleViewModel: ScheduleViewModel = viewModel(factory = factory)
    val venuesViewModel: VenuesViewModel = viewModel(factory = factory)
    val notificationsViewModel: NotificationsViewModel = viewModel(factory = factory)
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)

    val unreadNotifCount by notificationsViewModel.uiState.collectAsState()

    // Handle Auth Effects
    LaunchedEffect(authViewModel) {
        authViewModel.effects.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToBiometric -> currentScreen = Screen.Biometric
                is AuthEffect.NavigateToSync -> currentScreen = Screen.Syncing
                is AuthEffect.NavigateToHome -> currentScreen = Screen.Home
                else -> {}
            }
        }
    }

    // Back handling for sub-screens
    if (currentScreen != Screen.Home && currentScreen != Screen.Welcome) {
        BackHandler {
            when (currentScreen) {
                Screen.Login -> currentScreen = Screen.Welcome
                Screen.Biometric -> currentScreen = Screen.Login
                Screen.Syncing -> currentScreen = Screen.Home
                Screen.Schedule, Screen.Venues, Screen.Notifications, Screen.Profile -> currentScreen = Screen.Home
                else -> currentScreen = Screen.Home
            }
        }
    }

    val navItems = listOf(
        NavItem(Screen.Home, "Home", Icons.Default.Home, "nav_home"),
        NavItem(Screen.Schedule, "Schedule", Icons.Default.CalendarMonth, "nav_schedule"),
        NavItem(Screen.Venues, "Venues", Icons.Default.MeetingRoom, "nav_venues"),
        NavItem(Screen.Notifications, "Alerts", Icons.Default.Notifications, "nav_alerts"),
        NavItem(Screen.Profile, "Profile", Icons.Default.Person, "nav_profile")
    )

    val showBottomBar = currentScreen in listOf(
        Screen.Home,
        Screen.Schedule,
        Screen.Venues,
        Screen.Notifications,
        Screen.Profile
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .border(width = 0.75.dp, color = MaterialTheme.colorScheme.outlineVariant),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = item.screen },
                            icon = {
                                if (item.screen == Screen.Notifications && unreadNotifCount.unreadCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = TimyCrimson,
                                                contentColor = Color.White
                                            ) {
                                                Text(
                                                    text = unreadNotifCount.unreadCount.toString(),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = XBlue,
                                selectedTextColor = XBlue,
                                indicatorColor = Color.Transparent, // Pure clean X style without pill box
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag(item.tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Welcome -> WelcomeScreen(
                    onGetStarted = { currentScreen = Screen.Login },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = toggleTheme
                )
                Screen.Login -> LoginScreen(
                    viewModel = authViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = toggleTheme
                )
                Screen.Biometric -> BiometricScreen(
                    viewModel = authViewModel
                )
                Screen.Syncing -> SyncingScreen(
                    viewModel = authViewModel
                )
                Screen.Home -> HomeScreen(
                    viewModel = dashboardViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = toggleTheme,
                    onNotificationsClick = { currentScreen = Screen.Notifications },
                    onProfileClick = { currentScreen = Screen.Profile }
                )
                Screen.Schedule -> ScheduleScreen(
                    viewModel = scheduleViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = toggleTheme
                )
                Screen.Venues -> VenuesScreen(
                    viewModel = venuesViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = toggleTheme
                )
                Screen.Notifications -> NotificationsScreen(
                    viewModel = notificationsViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = toggleTheme
                )
                Screen.Profile -> ProfileScreen(
                    viewModel = profileViewModel,
                    onLoggedOut = { currentScreen = Screen.Welcome }
                )
            }
        }
    }
}
