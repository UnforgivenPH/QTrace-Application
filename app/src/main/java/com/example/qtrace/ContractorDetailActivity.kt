package com.example.qtrace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qtrace.adapters.ProjectAdapter
import com.example.qtrace.models.Contractor
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore

class ContractorDetailActivity : AppCompatActivity() {

    private lateinit var recyclerActive: RecyclerView
    private lateinit var recyclerCompleted: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contractor_detail)

        // 1. Get Data from Intent
        val contractor = intent.getSerializableExtra("CONTRACTOR_DATA") as? Contractor
        if (contractor == null) {
            Toast.makeText(this, "Error: Data missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. 🛠️ TOP NAV: Setup Custom Header Back Button
        // We preserved the 'btnBack' ID in the updated XML
        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // 3. UI BINDING: Basic Info
        findViewById<TextView>(R.id.tv_detail_contractor_name).text = contractor.name
        findViewById<TextView>(R.id.tv_detail_contractor_expertise).text =
            contractor.expertise?.joinToString(", ") ?: "General Engineering"

        findViewById<TextView>(R.id.tv_detail_contractor_contact).text =
            "Person: ${contractor.contactPerson?.takeIf { it.isNotBlank() } ?: "N/A"}\n" +
                    "Phone: ${contractor.phone?.takeIf { it.isNotBlank() } ?: "N/A"}"

        findViewById<TextView>(R.id.tv_detail_contractor_email).text = "Email: ${contractor.email ?: "N/A"}"
        findViewById<TextView>(R.id.tv_detail_contractor_address).text = "Address: ${contractor.address ?: "N/A"}"

        val imgLogo = findViewById<ImageView>(R.id.img_contractor_logo)
        if (contractor.logo != null && contractor.logo.path.isNotEmpty()) {
            Glide.with(this)
                .load(contractor.logo.path)
                .placeholder(R.color.image_placeholder)
                .into(imgLogo)
        }

        // 4. Setup RecyclerViews
        recyclerActive = findViewById(R.id.recyclerActiveProjects)
        recyclerActive.layoutManager = LinearLayoutManager(this)

        recyclerCompleted = findViewById(R.id.recyclerCompletedProjects)
        recyclerCompleted.layoutManager = LinearLayoutManager(this)

        // 5. Load and Filter Projects
        loadAllProjectsAndFilter(contractor)
    }

    private fun loadAllProjectsAndFilter(contractor: Contractor) {
        db.collection("projects").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.e("ContractorDetail", "Firestore Error", e)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val allProjects = snapshots.toObjects(Project::class.java)
                val contractorsProjects = ArrayList<Project>()

                for (project in allProjects) {
                    // Match by ID OR Name (Matches ContractorsFragment logic)
                    val isIdMatch = project.contractorId == contractor.id
                    val isNameMatch = project.contractor.trim().equals(contractor.name.trim(), ignoreCase = true)

                    if (isIdMatch || isNameMatch) {
                        contractorsProjects.add(project)
                    }
                }
                updateUI(contractorsProjects)
            }
        }
    }

    private fun updateUI(myProjects: List<Project>) {
        // Filter logic: Anything NOT "Finished" is Active
        val active = myProjects.filter { !it.status.equals("Finished", ignoreCase = true) }
        val completed = myProjects.filter { it.status.equals("Finished", ignoreCase = true) }

        // Update Counts (Preserved IDs)
        findViewById<TextView>(R.id.tv_active_project_count).text = "${active.size}"
        findViewById<TextView>(R.id.tv_completed_project_count).text = "${completed.size}"

        // 🛠️ ACTIVE LIST TOGGLE
        val tvNoActive = findViewById<TextView>(R.id.tvNoActiveProjects)
        if (active.isEmpty()) {
            tvNoActive.visibility = View.VISIBLE
            recyclerActive.visibility = View.GONE
        } else {
            tvNoActive.visibility = View.GONE
            recyclerActive.visibility = View.VISIBLE
            recyclerActive.adapter = ProjectAdapter(active) { openProject(it) }
        }

        // 🛠️ COMPLETED LIST TOGGLE
        val tvNoCompleted = findViewById<TextView>(R.id.tvNoCompletedProjects)
        if (completed.isEmpty()) {
            tvNoCompleted.visibility = View.VISIBLE
            recyclerCompleted.visibility = View.GONE
        } else {
            tvNoCompleted.visibility = View.GONE
            recyclerCompleted.visibility = View.VISIBLE
            recyclerCompleted.adapter = ProjectAdapter(completed) { openProject(it) }
        }
    }

    private fun openProject(project: Project) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("PROJECT_DATA", project)
        startActivity(intent)
    }
}