package com.example.qtrace

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.adapters.MilestoneAdapter
import com.example.qtrace.models.Contractor
import com.example.qtrace.models.Project
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var project: Project
    private val db = FirebaseFirestore.getInstance()

    // To stop listening when we leave the screen (prevents crashes/battery drain)
    private var snapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 1. Get Initial Data from Intent (Fast Load)
        project = intent.getSerializableExtra("PROJECT_DATA") as? Project ?: return

        // 2. Setup UI initially
        updateUI()

        // 3. Start Listening for Real-Time Updates
        listenForUpdates()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun listenForUpdates() {
        // Use the ID from the intent data to find the specific document
        if (project.id.isEmpty()) return

        snapshotListener = db.collection("projects").document(project.id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("DetailActivity", "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Update the local project object with fresh data
                    val freshProject = snapshot.toObject(Project::class.java)
                    if (freshProject != null) {
                        // Ensure ID is preserved if @DocumentId didn't catch it
                        freshProject.id = snapshot.id
                        this.project = freshProject

                        // Refresh the screen
                        updateUI()
                    }
                }
            }
    }

    // Moved UI binding logic here so we can call it multiple times
    private fun updateUI() {
        setupHeader()
        setupTabs()     // Refreshes the Milestone List
        fetchContractor() // Refreshes Contractor Name (in case it changed)
    }

    private fun setupHeader() {
        val title = findViewById<TextView>(R.id.tvDetailTitle)
        val desc = findViewById<TextView>(R.id.tvDetailDesc)
        val loc = findViewById<TextView>(R.id.tvDetailLocation)
        val budget = findViewById<TextView>(R.id.tvDetailBudget)
        val timeline = findViewById<TextView>(R.id.tvDetailTimeline)
        val cat = findViewById<TextView>(R.id.tvDetailCategory)
        val status = findViewById<TextView>(R.id.tvStatusBadge)

        title.text = project.title
        desc.text = project.description
        loc.text = "${project.address.street}, ${project.address.city}"
        cat.text = project.category

        // Status Logic (Colors)
        status.text = project.status
        when(project.status) {
            "Finished" -> {
                status.setTextColor(Color.parseColor("#198754")) // Green
                status.setBackgroundResource(R.drawable.bg_status_finished) // Ensure this drawable exists or use a color
            }
            "Delayed" -> {
                status.setTextColor(Color.parseColor("#DC3545")) // Red
                status.setBackgroundResource(R.drawable.bg_status_delayed)
            }
            else -> {
                status.setTextColor(Color.parseColor("#0D6EFD")) // Blue
                status.setBackgroundResource(R.drawable.bg_status_ongoing)
            }
        }

        val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
        budget.text = format.format(project.budget)

        val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val start = if (project.dates.started != null) sdf.format(project.dates.started) else "?"
        val end = if (project.dates.end != null) sdf.format(project.dates.end) else "?"
        timeline.text = "$start - $end"
    }

    private fun fetchContractor() {
        val tvName = findViewById<TextView>(R.id.tvContractorName)
        val btnProfile = findViewById<TextView>(R.id.btnViewContractor)

        // 1. Get the value stored in the project (could be ID or Name)
        val storedValue = if (project.contractorId.isNotEmpty()) project.contractorId else project.contractor

        if (storedValue.isEmpty()) {
            tvName.text = "No Contractor Assigned"
            btnProfile.visibility = View.GONE
            return
        }

        // 2. Try fetching as if it is an ID first (Fastest)
        db.collection("contractors").document(storedValue).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // ✅ It was an ID! We found the document directly.
                    setupContractorUI(doc.toObject(Contractor::class.java), doc.id)
                } else {
                    // ❌ It wasn't an ID (or ID was wrong).
                    // 3. FALLBACK: Search for it as a NAME.
                    searchContractorByName(storedValue)
                }
            }
            .addOnFailureListener {
                // If the direct fetch failed (e.g., invalid format), try searching as name
                searchContractorByName(storedValue)
            }
    }

    private fun searchContractorByName(name: String) {
        // Query the database: Find contractor where 'name' equals the string we have
        db.collection("contractors")
            .whereEqualTo("name", name)
            .limit(1) // We only need one match
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // ✅ Found a match by name!
                    val doc = documents.documents[0]
                    setupContractorUI(doc.toObject(Contractor::class.java), doc.id)
                } else {
                    // ❌ Neither ID nor Name matched anything
                    findViewById<TextView>(R.id.tvContractorName).text = name // Just show the name we have
                    findViewById<TextView>(R.id.btnViewContractor).visibility = View.GONE
                }
            }
            .addOnFailureListener {
                findViewById<TextView>(R.id.tvContractorName).text = name
                Log.e("DetailActivity", "Error searching contractor", it)
            }
    }

    private fun setupContractorUI(contractor: Contractor?, docId: String) {
        val tvName = findViewById<TextView>(R.id.tvContractorName)
        val btnProfile = findViewById<TextView>(R.id.btnViewContractor)

        if (contractor != null) {
            contractor.id = docId // Ensure ID is attached
            tvName.text = contractor.name
            btnProfile.visibility = View.VISIBLE

            btnProfile.setOnClickListener {
                val intent = Intent(this, ContractorDetailActivity::class.java)
                intent.putExtra("CONTRACTOR_DATA", contractor)
                startActivity(intent)
            }
        }
    }
    private fun setupTabs() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val overview = findViewById<CardView>(R.id.sectionOverview)
        val recycler = findViewById<RecyclerView>(R.id.recyclerMilestones)
        val emptyMsg = findViewById<TextView>(R.id.tvEmptyTab)

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // RELOAD ADAPTER with new milestones
        val adapter = MilestoneAdapter(project.milestones)
        recycler.adapter = adapter

        // Ensure the correct visibility based on the currently selected tab
        updateTabVisibility(tabLayout.selectedTabPosition)

        // Remove old listeners to prevent duplicates if updateUI is called multiple times
        tabLayout.clearOnTabSelectedListeners()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateTabVisibility(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateTabVisibility(position: Int) {
        val overview = findViewById<CardView>(R.id.sectionOverview)
        val recycler = findViewById<RecyclerView>(R.id.recyclerMilestones)
        val emptyMsg = findViewById<TextView>(R.id.tvEmptyTab)

        overview.visibility = View.GONE
        recycler.visibility = View.GONE
        emptyMsg.visibility = View.GONE

        when (position) {
            0 -> overview.visibility = View.VISIBLE
            1 -> emptyMsg.visibility = View.VISIBLE // Documents
            2 -> { // Photo Gallery
                if (project.milestones.isEmpty()) {
                    emptyMsg.text = "No milestones photos yet."
                    emptyMsg.visibility = View.VISIBLE
                } else {
                    recycler.visibility = View.VISIBLE
                }
            }
            3 -> emptyMsg.visibility = View.VISIBLE // Reports
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop listening when the activity closes
        snapshotListener?.remove()
    }
}