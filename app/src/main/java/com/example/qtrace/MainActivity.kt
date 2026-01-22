package com.example.qtrace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.qtrace.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Setup Navigation Listener
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { loadFragment(HomeFragment()); true }
                R.id.nav_explore -> { loadFragment(ExploreFragment()); true }
                R.id.nav_contractors -> { loadFragment(ContractorsFragment()); true }
                R.id.nav_news -> { loadFragment(NewsFragment()); true }
                R.id.nav_account -> { loadFragment(AccountFragment()); true }
                else -> false
            }
        }

        // 🛠️ KEY CHANGE: Check if we should open a specific tab (like Account)
        if (savedInstanceState == null) {
            val target = intent.getStringExtra("TARGET_FRAGMENT")

            if (target == "ACCOUNT") {
                // Load Account Fragment
                loadFragment(AccountFragment())
                // Update the bottom nav visual state
                bottomNav.selectedItemId = R.id.nav_account
            } else {
                // Default to Home
                loadFragment(HomeFragment())
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}