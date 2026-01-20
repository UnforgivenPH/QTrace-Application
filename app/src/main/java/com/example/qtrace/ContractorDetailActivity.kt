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

    private lateinit var recycler: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contractor_detail)

        // 1. Get Data
        val contractor = intent.getSerializableExtra("CONTRACTOR_DATA") as? Contractor
        if (contractor == null) {
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. Bind Data to Your Layout Views
        findViewById<TextView>(R.id.tv_detail_contractor_name).text = contractor.name

        // Formatting lists nicely
        val expertiseText = contractor.expertise?.joinToString("\n• ", prefix = "• ") ?: "General"
        findViewById<TextView>(R.id.tv_detail_contractor_expertise).text = expertiseText

        findViewById<TextView>(R.id.tv_detail_contractor_contact).text =
            "Person: ${contractor.contactPerson}\nPhone: ${contractor.phone}"

        findViewById<TextView>(R.id.tv_detail_contractor_email).text = "Email: ${contractor.email}"
        findViewById<TextView>(R.id.tv_detail_contractor_address).text = contractor.address

        // Load Logo
        val imgLogo = findViewById<ImageView>(R.id.img_contractor_logo)
        if (contractor.logo.path.isNotEmpty()) {
            Glide.with(this)
                .load(contractor.logo.path)
                .placeholder(android.R.drawable.ic_menu_my_calendar)
                .into(imgLogo)
        }

        // 3. Setup Project List
        recycler = findViewById(R.id.recyclerContractorProjects)
        recycler.layoutManager = LinearLayoutManager(this)

        // Load the projects
        loadContractorProjects(contractor.name)
    }

    private fun loadContractorProjects(contractorName: String) {
        // NOTE: Ensure your Firestore 'projects' collection has a field 'contractor' matching this name
        db.collection("projects")
            .whereEqualTo("contractor", contractorName)
            .get()
            .addOnSuccessListener { result ->
                val projects = result.toObjects(Project::class.java)

                if (projects.isEmpty()) {
                    findViewById<TextView>(R.id.tvNoProjects).visibility = View.VISIBLE
                } else {
                    findViewById<TextView>(R.id.tvNoProjects).visibility = View.GONE
                    val adapter = ProjectAdapter(projects) { project ->
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("PROJECT_DATA", project)
                        startActivity(intent)
                    }
                    recycler.adapter = adapter
                }
            }
            .addOnFailureListener {
                Log.e("ContractorDetail", "Error loading projects", it)
            }
    }
}