package com.example.qtrace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.qtrace.fragments.ContractorsFragment
import com.example.qtrace.fragments.HomeFragment // Fallback if needed
import com.example.qtrace.fragments.MapFragment
import com.example.qtrace.fragments.NewsFragment
import com.example.qtrace.fragments.ProjectFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Default Load (Projects)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_projects -> ProjectFragment()
                R.id.nav_map -> MapFragment()
                R.id.nav_contractors -> ContractorsFragment()
                R.id.nav_news -> NewsFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}