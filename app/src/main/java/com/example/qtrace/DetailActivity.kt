package com.example.qtrace

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.adapters.MilestoneAdapter
import com.example.qtrace.models.Contractor
import com.example.qtrace.models.Project
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var project: Project
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 1. Get Project Data
        project = intent.getSerializableExtra("PROJECT_DATA") as? Project ?: return

        setupHeader()
        setupTabs()
        fetchContractor()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupHeader() {
        // Find Views
        val title = findViewById<TextView>(R.id.tvDetailTitle)
        val desc = findViewById<TextView>(R.id.tvDetailDesc)
        val loc = findViewById<TextView>(R.id.tvDetailLocation)
        val budget = findViewById<TextView>(R.id.tvDetailBudget)
        val timeline = findViewById<TextView>(R.id.tvDetailTimeline)
        val cat = findViewById<TextView>(R.id.tvDetailCategory)
        val status = findViewById<TextView>(R.id.tvStatusBadge)

        // Bind Data
        title.text = project.title
        desc.text = project.description
        loc.text = "${project.address.street}, ${project.address.city}"
        cat.text = project.category
        status.text = project.status

        // Format Budget
        val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
        budget.text = format.format(project.budget)

        // Format Dates
        val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val start = if (project.dates.started != null) sdf.format(project.dates.started) else "?"
        val end = if (project.dates.end != null) sdf.format(project.dates.end) else "?"
        timeline.text = "$start - $end"
    }

    private fun fetchContractor() {
        val tvName = findViewById<TextView>(R.id.tvContractorName)
        val btnProfile = findViewById<TextView>(R.id.btnViewContractor)

        if (project.contractorId.isNotEmpty()) {
            db.collection("contractors").document(project.contractorId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val contractor = doc.toObject(Contractor::class.java)
                        contractor?.id = doc.id
                        tvName.text = contractor?.name ?: "Unknown"

                        btnProfile.setOnClickListener {
                            val intent = Intent(this, ContractorDetailActivity::class.java)
                            intent.putExtra("CONTRACTOR_DATA", contractor)
                            startActivity(intent)
                        }
                    } else {
                        tvName.text = "Contractor Not Found"
                    }
                }
        } else {
            tvName.text = "No Contractor Assigned"
            btnProfile.visibility = View.GONE
        }
    }

    private fun setupTabs() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val overview = findViewById<CardView>(R.id.sectionOverview)
        val recycler = findViewById<RecyclerView>(R.id.recyclerMilestones)
        val emptyMsg = findViewById<TextView>(R.id.tvEmptyTab)

        // Setup Recycler for Gallery
        recycler.layoutManager = LinearLayoutManager(this)
        // Convert Milestones List to Adapter
        val adapter = MilestoneAdapter(project.milestones)
        recycler.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Reset Visibility
                overview.visibility = View.GONE
                recycler.visibility = View.GONE
                emptyMsg.visibility = View.GONE

                when (tab?.position) {
                    0 -> overview.visibility = View.VISIBLE // Overview
                    1 -> emptyMsg.visibility = View.VISIBLE // Documents (Placeholder)
                    2 -> { // Photo Gallery
                        if (project.milestones.isEmpty()) emptyMsg.visibility = View.VISIBLE
                        else recycler.visibility = View.VISIBLE
                    }
                    3 -> emptyMsg.visibility = View.VISIBLE // Reports (Placeholder)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}