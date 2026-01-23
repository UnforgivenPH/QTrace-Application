package com.example.qtrace.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.DetailActivity
import com.example.qtrace.R
import com.example.qtrace.ReportFormActivity
import com.example.qtrace.adapters.ProjectAdapter
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recycler: RecyclerView

    // UI Elements
    private lateinit var tvUserName: TextView
    private lateinit var tvActive: TextView
    private lateinit var tvFinished: TextView
    private lateinit var tvBudget: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize Views
        tvUserName = view.findViewById(R.id.tvUserName)
        tvActive = view.findViewById(R.id.tvStatActive)
        tvFinished = view.findViewById(R.id.tvStatFinished)
        tvBudget = view.findViewById(R.id.tvTotalBudget)

        recycler = view.findViewById(R.id.recyclerRecent)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // 2. Link the Report Button
        val reportBtn = view.findViewById<Button>(R.id.btn_report_issue)
        reportBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Report feature coming soon", Toast.LENGTH_SHORT).show()
        }

        // 3. Load Data
        loadUserInfo()
        loadDashboardStats()
    }

    private fun loadUserInfo() {
        // 🛠️ FIX: Read from SharedPreferences (Local Session) instead of FirebaseAuth
        val sharedPref = requireContext().getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", null)

        if (userId != null) {
            // User IS logged in -> Fetch name from Firestore using the saved ID
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val first = doc.getString("fullName.first") ?: "User"
                        val last = doc.getString("fullName.last") ?: ""
                        tvUserName.text = "Hello, $first $last"
                    }
                }
                .addOnFailureListener {
                    tvUserName.text = "Hello, User"
                }
        } else {
            // User is NOT logged in -> Guest Mode
            tvUserName.text = "Hello, Guest User"
        }
    }

    private fun loadDashboardStats() {
        db.collection("projects").get()
            .addOnSuccessListener { snapshots ->
                if (snapshots != null) {
                    val projects = snapshots.toObjects(Project::class.java)

                    // A. Calculate Counts (Includes "Delayed" in Active)
                    val activeCount = projects.count { !it.status.equals("Finished", ignoreCase = true) }
                    val finishedCount = projects.count { it.status.equals("Finished", ignoreCase = true) }
                    val totalBudget = projects.sumOf { it.budget }

                    // B. Update Stats UI
                    tvActive.text = activeCount.toString()
                    tvFinished.text = finishedCount.toString()

                    try {
                        val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
                        tvBudget.text = format.format(totalBudget)
                    } catch (e: Exception) {
                        tvBudget.text = "₱${totalBudget}"
                    }

                    // C. Populate Recent List (Take first 3)
                    val recentProjects = projects.take(3)
                    val adapter = ProjectAdapter(recentProjects) { project ->
                        val intent = Intent(requireContext(), DetailActivity::class.java)
                        intent.putExtra("PROJECT_DATA", project)
                        startActivity(intent)
                    }
                    recycler.adapter = adapter
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error loading dashboard", e)
            }
    }
    // Add this inside HomeFragment.kt
    override fun onResume() {
        super.onResume()
        // Reload user info every time the fragment becomes visible
        loadUserInfo()
    }
}