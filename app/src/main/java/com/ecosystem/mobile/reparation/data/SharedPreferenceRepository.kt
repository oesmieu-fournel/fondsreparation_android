package com.ecosystem.mobile.reparation.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ch.qos.logback.classic.Level
import com.sap.cloud.mobile.foundation.settings.policies.LogPolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val KEY_LOG_SETTING_PREFERENCE = "key.log.settings.preference"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class UserPreferences(
    val logSetting: LogPolicy
)

class SharedPreferenceRepository(private val context: Context) {

    private val dataStore
        get() = context.dataStore

    private object PreferencesKeys {
        val PREF_LOG_SETTING = stringPreferencesKey(KEY_LOG_SETTING_PREFERENCE)
    }

    suspend fun resetSharedPreference() {
        dataStore.edit {
            it.clear()
        }
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun updateLogLevel(logLevel: Level) {
        dataStore.edit { preferences ->
            val settings = LogPolicy.createFromJsonString(
                preferences[PreferencesKeys.PREF_LOG_SETTING] ?: LogPolicy().toString()
            )
            val updateSettings = settings.copy(logLevel = LogPolicy.getLogLevelString(logLevel))
            Log.d("log", "update settings as $updateSettings")
            preferences[PreferencesKeys.PREF_LOG_SETTING] = updateSettings.toString()
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val logSetting =
            preferences[PreferencesKeys.PREF_LOG_SETTING]?.let { LogPolicy.createFromJsonString(it) }
                ?: LogPolicy()
        return UserPreferences(
            logSetting
        )
    }
}
