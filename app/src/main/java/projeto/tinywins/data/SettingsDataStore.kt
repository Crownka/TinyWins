package projeto.tinywins.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tiny_wins_settings")


// Essa classe agora gerencia todas as configurações do meu app.

class SettingsDataStore(private val context: Context) {

    // --- Preferência de Tema Escuro ---
    private val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode_enabled")

    val themePreferenceFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_DARK_MODE_KEY] ?: false
        }

    suspend fun saveThemePreference(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE_KEY] = isDarkMode
        }
    }

    // --- Preferência de Notificações ---
    private val ARE_NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("are_notifications_enabled")

    val notificationsPreferenceFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ARE_NOTIFICATIONS_ENABLED_KEY] ?: true
        }

    suspend fun saveNotificationsPreference(areEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ARE_NOTIFICATIONS_ENABLED_KEY] = areEnabled
        }
    }

    // --- NOVA PREFERÊNCIA DE ANIMAÇÕES ---

    // 1. Minha nova chave para a configuração de animações.
    private val ARE_ANIMATIONS_ENABLED_KEY = booleanPreferencesKey("are_animations_enabled")

    // 2. Meu novo Flow para ler o estado das animações.
    //    O padrão será 'true', para que as animações venham ativadas.
    val animationsPreferenceFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ARE_ANIMATIONS_ENABLED_KEY] ?: true // Padrão: ativado
        }

    // 3. Minha nova função 'suspend' para salvar o estado das animações.
    suspend fun saveAnimationsPreference(areEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ARE_ANIMATIONS_ENABLED_KEY] = areEnabled
        }
    }
}