package com.practice.autocare.activities.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.practice.autocare.R
import com.practice.autocare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        чтобы избежать дублирования при повороте экрана
        if (savedInstanceState == null) {
            replaceFragment(CalendarFragment())
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
}