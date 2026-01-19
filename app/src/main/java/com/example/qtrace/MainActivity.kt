package com.example.qtrace

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var db: FirebaseFirestore

    // UI Elements for Summary Card
    private lateinit var summaryCard: CardView
    private lateinit var tvTitle: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvDesc: TextView
    private lateinit var btnViewDetails: Button

    private var selectedProject: Project? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSM Configuration (Important for caching)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_main)

        // Initialize UI
        summaryCard = findViewById(R.id.summaryCard)
        tvTitle = findViewById(R.id.tvSummaryTitle)
        tvCategory = findViewById(R.id.tvSummaryCategory)
        tvStatus = findViewById(R.id.tvSummaryStatus)
        tvDesc = findViewById(R.id.tvSummaryDesc)
        btnViewDetails = findViewById(R.id.btnViewDetails)

        setupMap()
        setupFirestore()

        // Handle "View Full Details" click
        btnViewDetails.setOnClickListener {
            selectedProject?.let { project ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("PROJECT_DATA", project)
                startActivity(intent)
            }
        }
    }

    private fun setupMap() {
        map = findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK) // Standard OSM Style
        map.setMultiTouchControls(true)

        // Center Map on Quezon City
        val mapController = map.controller
        mapController.setZoom(13.0)
        val startPoint = GeoPoint(14.6760, 121.0437) // QC Coordinates
        mapController.setCenter(startPoint)
    }

//    private fun setupFirestore() {
//        db = FirebaseFirestore.getInstance()
//
//        db.collection("projects")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    try {
//                        val project = document.toObject(Project::class.java)
//                        // Assign ID manually since it's not in the fields
//                        project.id = document.id
//                        addMarker(project)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Error loading data: ${exception.message}", Toast.LENGTH_LONG).show()
//            }
//    }

    private fun setupFirestore() {
        db = FirebaseFirestore.getInstance()

        // 1. CHECK THE COLLECTION NAME HERE (Case Sensitive!)
        db.collection("projects")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    println("DEBUG: Connection successful, but collection is EMPTY.")
                } else {
                    println("DEBUG: Found ${result.size()} documents.")
                    for (document in result) {
                        try {
                            println("DEBUG: Processing document ${document.id}")
                            val project = document.toObject(Project::class.java)

                            println("DEBUG: Parsed Title: ${project.title}")
                            println("DEBUG: Lat: ${project.location.lat}, Lng: ${project.location.lng}")

                            // Check if coordinates are valid
                            if (project.location.lat != 0.0 && project.location.lng != 0.0) {
                                project.id = document.id
                                addMarker(project)
                                println("DEBUG: Marker added!")
                            } else {
                                println("DEBUG: Coordinates are 0.0. Check Firestore field names!")
                            }
                        } catch (e: Exception) {
                            println("DEBUG: Error parsing data: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                println("DEBUG: Firestore Error: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun addMarker(project: Project) {
        val marker = Marker(map)
        marker.position = GeoPoint(project.location.lat, project.location.lng)
        marker.title = project.title

        // Handle Marker Click
        marker.setOnMarkerClickListener { _, _ ->
            showSummary(project)
            true // Return true to consume the event
        }

        map.overlays.add(marker)
        map.invalidate() // Refresh map
    }

    private fun showSummary(project: Project) {
        selectedProject = project

        tvTitle.text = project.title
        tvCategory.text = project.category
        tvStatus.text = "Status: ${project.status}"
        tvDesc.text = project.description

        summaryCard.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}