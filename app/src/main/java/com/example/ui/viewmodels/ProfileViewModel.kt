package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.datastore.UserPreferences
import com.example.data.repository.TimyRepository
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Collins Omondi",
    val regNo: String = "IN16/00142/23",
    val cohortId: String = "SE-Y3S1",
    val cohortName: String = "BSc. Software Engineering · Y3S1",
    val biometricEnabled: Boolean = false,
    val darkTheme: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.LIGHTS_OUT,
    val lastSyncTimestamp: Long = 0L,
    val isSyncing: Boolean = false,
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(
    private val repository: TimyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.userName.collect { name ->
                _uiState.update { it.copy(userName = name) }
            }
        }
        viewModelScope.launch {
            userPreferences.regNo.collect { reg ->
                if (reg != null) _uiState.update { it.copy(regNo = reg) }
            }
        }
        viewModelScope.launch {
            userPreferences.cohortId.collect { id ->
                _uiState.update { it.copy(cohortId = id) }
            }
        }
        viewModelScope.launch {
            userPreferences.cohortName.collect { name ->
                _uiState.update { it.copy(cohortName = name) }
            }
        }
        viewModelScope.launch {
            userPreferences.biometricEnabled.collect { enabled ->
                _uiState.update { it.copy(biometricEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            userPreferences.darkTheme.collect { dark ->
                _uiState.update { it.copy(darkTheme = dark) }
            }
        }
        viewModelScope.launch {
            userPreferences.themeMode.collect { modeStr ->
                val mode = when (modeStr) {
                    "LIGHT" -> AppThemeMode.LIGHT
                    "DIM" -> AppThemeMode.DIM
                    else -> AppThemeMode.LIGHTS_OUT
                }
                _uiState.update { it.copy(themeMode = mode, darkTheme = mode != AppThemeMode.LIGHT) }
            }
        }
        viewModelScope.launch {
            userPreferences.lastSyncTimestamp.collect { ts ->
                _uiState.update { it.copy(lastSyncTimestamp = ts) }
            }
        }
    }

    fun switchCohort(id: String, name: String) {
        viewModelScope.launch {
            userPreferences.updateCohort(id, name)
            repository.syncTimetable(id)
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setBiometricEnabled(enabled)
        }
    }

    fun toggleTheme(dark: Boolean) {
        viewModelScope.launch {
            val targetMode = if (dark) AppThemeMode.LIGHTS_OUT else AppThemeMode.LIGHT
            userPreferences.setThemeMode(targetMode.name)
        }
    }

    fun selectThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode.name)
        }
    }

    fun forceSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            repository.syncTimetable(_uiState.value.cohortId)
            repository.refreshVenues()
            repository.fetchNotifications()
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}
