package com.practice.autocare.activities.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practice.autocare.R

//abstract class BaseActivity : AppCompatActivity() {
//    protected fun setupBottomNavigation(selectedItemId: Int) {
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        bottomNavigationView.selectedItemId = selectedItemId
//
//        bottomNavigationView.setOnItemSelectedListener { item ->
//            val targetActivity = when (item.itemId) {
//                R.id.nav_calendar -> CalendarActivity::class.java
//                R.id.nav_history -> HistoryActivity::class.java
//                else -> null
//            }
//
//            if (targetActivity != null && this::class != targetActivity) {
//                startActivity(Intent(this, targetActivity))
//                overridePendingTransition(0, 0)
//                finish()
//                true
//            } else {
//                false
//            }
//        }
//    }
//}