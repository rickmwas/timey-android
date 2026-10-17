package com.example.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timy_user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_REG_NO = stringPreferencesKey("reg_no")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_COHORT_ID = stringPreferencesKey("cohort_id")
        val KEY_COHORT_NAME = stringPreferencesKey("cohort_name")
        val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_LAST_SYNC = longPreferencesKey("last_sync_timestamp")
        val KEY_ONBOARDED = booleanPreferencesKey("is_onboarded")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[KEY_ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[KEY_REFRESH_TOKEN] }
    val regNo: Flow<String?> = context.dataStore.data.map { it[KEY_REG_NO] }
    val userName: Flow<String> = context.dataStore.data.map { it[KEY_USER_NAME] ?: "Student" }
    val cohortId: Flow<String> = context.dataStore.data.map { it[KEY_COHORT_ID] ?: "SE-Y3S1" }
    val cohortName: Flow<String> = context.dataStore.data.map {
        it[KEY_COHORT_NAME] ?: "BSc. Software Engineering · Y3S1"
    }
    val biometricEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_BIOMETRIC_ENABLED] ?: false }
    val darkTheme: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK_THEME] ?: true }
    val themeMode: Flow<String> = context.dataStore.data.map { it[KEY_THEME_MODE] ?: "LIGHTS_OUT" }
    val lastSyncTimestamp: Flow<Long> = context.dataStore.data.map { it[KEY_LAST_SYNC] ?: 0L }
    val isOnboarded: Flow<Boolean> = context.dataStore.data.map { it[KEY_ONBOARDED] ?: false }

    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String,
        regNo: String,
        name: String,
        cohortId: String,
        cohortName: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
            prefs[KEY_USER_ID] = userId
            prefs[KEY_REG_NO] = regNo
            prefs[KEY_USER_NAME] = name
            prefs[KEY_COHORT_ID] = cohortId
            prefs[KEY_COHORT_NAME] = cohortName
            prefs[KEY_ONBOARDED] = true
        }
    }

    suspend fun updateCohort(cohortId: String, cohortName: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_COHORT_ID] = cohortId
            prefs[KEY_COHORT_NAME] = cohortName
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_THEME] = enabled
            prefs[KEY_THEME_MODE] = if (enabled) "LIGHTS_OUT" else "LIGHT"
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
            prefs[KEY_DARK_THEME] = (mode != "LIGHT")
        }
    }

    suspend fun setLastSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_SYNC] = timestamp }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_REG_NO)
            prefs.remove(KEY_USER_NAME)
            prefs.remove(KEY_ONBOARDED)
        }
    }
}
