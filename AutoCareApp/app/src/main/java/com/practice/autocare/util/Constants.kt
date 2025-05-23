package com.practice.autocare.util

import android.content.Context
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.practice.autocare.activities.auth.StartActivity

class Constants {
    companion object {
        const val BASE_URL = "http://192.168.1.84:8080"
        lateinit var MAIN: StartActivity

        fun setupErrorClearingOnTextChanged(
            editText: TextInputEditText,
            textInputLayout: TextInputLayout
        ) {
            editText.doOnTextChanged { text, start, before, count ->
                if (before == 0 && count == 1) {
                    textInputLayout.error = null
                }
            }
        }


        private const val PREFS_NAME = "AutoCarePrefs"
        private const val KEY_EMAIL = "user_email"

        fun saveUserEmail(email: String) {
            MAIN.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_EMAIL, email)
                .apply()
        }

        fun getUserEmail(): String? {
            return MAIN.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_EMAIL, null)
        }
    }
}