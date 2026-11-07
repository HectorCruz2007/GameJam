package com.zukesito.gamejam.data.local

import android.content.Context
import androidx.core.content.edit

// Stores the token in private SharedPreferences
class SharedPreferencesSessionStorage(context: Context) : SessionStorage {

    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getToken(): String? = preferences.getString(KEY_TOKEN, null)

    override fun saveToken(token: String) {
        preferences.edit { putString(KEY_TOKEN, token) }
    }

    override fun clear() {
        preferences.edit { remove(KEY_TOKEN) }
    }

    private companion object {
        const val PREFERENCES_NAME = "session_preferences"
        const val KEY_TOKEN = "access_token"
    }
}
