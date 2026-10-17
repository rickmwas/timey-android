package com.example.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.core.database.TimyDatabase
import com.example.core.datastore.UserPreferences
import com.example.core.network.NetworkClient
import com.example.data.repository.TimyRepository
import com.example.data.repository.TimyRepositoryImpl

class ViewModelFactory(
    private val repository: TimyRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository, userPreferences) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(repository, userPreferences) as T
            }
            modelClass.isAssignableFrom(ScheduleViewModel::class.java) -> {
                ScheduleViewModel(repository, userPreferences) as T
            }
            modelClass.isAssignableFrom(VenuesViewModel::class.java) -> {
                VenuesViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NotificationsViewModel::class.java) -> {
                NotificationsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository, userPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        fun create(context: Context): ViewModelFactory {
            val appCtx = context.applicationContext
            val db = TimyDatabase.getInstance(appCtx)
            val prefs = UserPreferences(appCtx)
            val apiService = NetworkClient.createApiService(prefs)
            val repo = TimyRepositoryImpl(db, apiService, prefs)
            return ViewModelFactory(repo, prefs)
        }
    }
}
