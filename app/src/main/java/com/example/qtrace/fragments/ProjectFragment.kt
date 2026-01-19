package com.example.qtrace.fragments

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.qtrace.DetailActivity // Import your existing Activity
import com.example.qtrace.R
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Note: Constructor takes the layout ID
class ProjectsFragment : Fragment(R.layout.fragment_projects) {

    private lateinit var map: MapView
    private lateinit var db: FirebaseFirestore

    // UI Elements
    private lateinit var summaryCard: CardView
    private lateinit var tvTitle: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvDesc: TextView
    private lateinit var btnViewDetails: Button

    private var selectedProject: Project? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // OSM Config (Use requireContext() in fragments)
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))

        // Initialize UI (use view.findViewById)
        summaryCard = view.findViewById(R.id.summaryCard)
        tvTitle = view.findViewById(R.id.tvSummaryTitle)
        tvCategory = view.findViewById(R.id.tvSummaryCategory)
        tvStatus = view.findViewById(R.id.tvSummaryStatus)
        tvDesc = view.findViewById(R.id.tvSummaryDesc)
        btnViewDetails = view.findViewById(R.id.btnViewDetails)
        map = view.findViewById(R.id.mapView)

        setupMap()
        setupFirestore()

        btnViewDetails.setOnClickListener {
            selectedProject?.let { project ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("PROJECT_DATA", project) // Matches your key in DetailActivity
                startActivity(intent)
            }
        }
    }

    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(13.0)
        val startPoint = GeoPoint(14.6760, 121.0437)
        mapController.setCenter(startPoint)
    }

    private fun setupFirestore() {
        db = FirebaseFirestore.getInstance()

        db.collection("projects")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    try {
                        val project = document.toObject(Project::class.java)
                        project.id = document.id
                        addMarker(project)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Use context for Toast in Fragment
                context?.let {
                    Toast.makeText(it, "Error loading data: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun addMarker(project: Project) {
        val marker = Marker(map)
        // Accessing your nested GeoLocation object
        marker.position = GeoPoint(project.location.lat, project.location.lng)
        marker.title = project.title

        marker.setOnMarkerClickListener { _, _ ->
            showSummary(project)
            true
        }

        map.overlays.add(marker)
        map.invalidate()
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