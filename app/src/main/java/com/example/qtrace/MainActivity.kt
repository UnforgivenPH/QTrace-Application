package com.example.qtrace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.qtrace.fragments.HomeFragment
import com.example.qtrace.fragments.ProjectsFragment
import com.example.qtrace.fragments.ReportsFragment
import com.example.qtrace.fragments.ContractorsFragment
import com.example.qtrace.fragments.NewsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Load Default Fragment
        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_projects -> loadFragment(ProjectsFragment())

                // Add this new case
                R.id.nav_contractors -> loadFragment(ContractorsFragment())

                R.id.nav_reports -> loadFragment(ReportsFragment())
                R.id.nav_news -> loadFragment(NewsFragment()) // Ensure NewsFragment exists
                else -> false
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}