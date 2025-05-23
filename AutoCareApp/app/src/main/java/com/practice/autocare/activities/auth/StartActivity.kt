package com.practice.autocare.activities.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.practice.autocare.R
import com.practice.autocare.activities.main.MainActivity
import com.practice.autocare.databinding.ActivityStartBinding
import com.practice.autocare.util.Constants.Companion.KEY_EMAIL
import com.practice.autocare.util.Constants.Companion.MAIN
import com.practice.autocare.util.Constants.Companion.PREFS_NAME

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверяем авторизацию ДО установки контента
        if (isUserLoggedIn()) {
            startMainActivity()
            finish()
            return
        }

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MAIN = this

        navController = Navigation.findNavController(this, R.id.main_fragment)

    }

    private fun isUserLoggedIn(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_EMAIL, null) != null
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}