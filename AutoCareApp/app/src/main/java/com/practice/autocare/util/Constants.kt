package com.practice.autocare.util

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
    }
}