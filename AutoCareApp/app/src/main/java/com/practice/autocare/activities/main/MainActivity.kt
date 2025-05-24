package com.practice.autocare.activities.main

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.practice.autocare.R
import com.practice.autocare.activities.auth.StartActivity
import com.practice.autocare.databinding.ActivityMainBinding
import com.practice.autocare.util.Constants

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        setSupportActionBar(binding.mainToolbar)
        // todo setSupportActionBar?.title = машина пользователя (марка и модель)

        binding.navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.mainToolbar,
            R.string.open_nav,
            R.string.close_nav,
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // чтобы избежать дублирования при повороте экрана
        if (savedInstanceState == null) {
            replaceFragment(CalendarFragment())
            binding.navView.setCheckedItem(R.id.nav_calendar)
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_calendar -> replaceFragment(CalendarFragment())
                R.id.nav_history -> replaceFragment(HistoryFragment())
                else -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrameLayout,fragment)
        fragmentTransaction.commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_accSettings -> Toast.makeText(this, "посмотреть аккаунт", Toast.LENGTH_SHORT).show()
            R.id.nav_settings -> Toast.makeText(this, "посмотреть настройки", Toast.LENGTH_SHORT).show()
            R.id.nav_exit -> logout()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        // Очищаем SharedPreferences
        getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(Constants.KEY_EMAIL)
            .apply()

        // Возвращаемся на StartActivity
        startActivity(Intent(this, StartActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}