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

        replaceFragment(CalendarFragment.newInstance())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){
                R.id.nav_calendar -> replaceFragment(CalendarFragment.newInstance())
                R.id.nav_history -> replaceFragment(HistoryFragment.newInstance())
                else -> {
                    Toast.makeText(this, "error bottomNavigationView", Toast.LENGTH_SHORT).show()
                }
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