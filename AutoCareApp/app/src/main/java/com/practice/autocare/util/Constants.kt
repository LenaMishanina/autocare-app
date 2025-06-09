package com.practice.autocare.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.practice.autocare.activities.auth.StartActivity
import com.practice.autocare.models.service.ServiceEventResponse
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class Constants {
    companion object {
        const val BASE_URL = "http://192.168.1.84:8080"
        // "http://<YOUR_IP_ADDRESS>:8080"
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

        fun handleNetworkError(context: Context, e: Exception) {
            val message = when (e) {
                is ConnectException -> "Нет подключения к серверу"
                is SocketTimeoutException -> "Таймаут соединения"
                else -> "Ошибка: ${e.localizedMessage}"
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }



        const val PREFS_NAME = "AutoCarePrefs"
        const val KEY_EMAIL = "user_email"

        fun saveUserEmail(context: Context, email: String) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_EMAIL, email)
                .apply()
        }

        fun getUserEmail(context: Context): String? {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_EMAIL, null)
        }
    }
}