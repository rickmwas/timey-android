package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.datastore.UserPreferences
import com.example.data.repository.TimyRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val admissionNo: String = "IN16/00142/23",
    val password: String = "Kisii@2026",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val cohortName: String = "BSc. Software Engineering · Y3S1",
    val syncProgress: Float = 0f,
    val syncStatusText: String = "Preparing local Room database..."
)

sealed interface AuthEffect {
    data object NavigateToBiometric : AuthEffect
    data object NavigateToSync : AuthEffect
    data object NavigateToHome : AuthEffect
    data class ShowToast(val message: String) : AuthEffect
}

class AuthViewModel(
    private val repository: TimyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<AuthEffect>()
    val effects: SharedFlow<AuthEffect> = _effects.asSharedFlow()

    fun onAdmissionNoChange(value: String) {
        _uiState.update { it.copy(admissionNo = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun submitLogin() {
        val adm = _uiState.value.admissionNo.trim()
        val pass = _uiState.value.password

        if (adm.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your university admission number.") }
            return
        }

        if (pass.length < 4) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 4 characters.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.login(adm, pass)
            result.onSuccess { session ->
                _uiState.update { it.copy(isLoading = false, cohortName = session.cohortName) }
                _effects.emit(AuthEffect.NavigateToBiometric)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Invalid credentials. Please verify your student ID."
                    )
                }
            }
        }
    }

    fun skipBiometrics() {
        viewModelScope.launch {
            userPreferences.setBiometricEnabled(false)
            _effects.emit(AuthEffect.NavigateToSync)
        }
    }

    fun enableBiometrics() {
        viewModelScope.launch {
            userPreferences.setBiometricEnabled(true)
            _effects.emit(AuthEffect.NavigateToSync)
        }
    }

    fun startInitialSync() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    syncProgress = 0.25f,
                    syncStatusText = "Connecting to Kisii University Timetable Gateway..."
                )
            }
            kotlinx.coroutines.delay(400)
            _uiState.update {
                it.copy(
                    syncProgress = 0.60f,
                    syncStatusText = "Validating cohort timetable and room availability..."
                )
            }
            repository.syncTimetable("SE-Y3S1")
            repository.refreshVenues()
            repository.fetchNotifications()

            _uiState.update {
                it.copy(
                    syncProgress = 0.90f,
                    syncStatusText = "Saving encrypted schedule to Room SQLite database..."
                )
            }
            kotlinx.coroutines.delay(300)
            _uiState.update {
                it.copy(
                    syncProgress = 1.0f,
                    syncStatusText = "Synchronized successfully. Academic schedule ready."
                )
            }
            kotlinx.coroutines.delay(300)
            _effects.emit(AuthEffect.NavigateToHome)
        }
    }
}
