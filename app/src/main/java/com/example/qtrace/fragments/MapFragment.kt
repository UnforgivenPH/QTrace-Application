package com.example.qtrace.fragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.adapters.ProjectAdapter
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private lateinit var map: MapView
    private lateinit var recycler: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private var allProjects = listOf<Project>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Map Setup
        map = view.findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(13.0)
        map.controller.setCenter(GeoPoint(14.6760, 121.0437))

        // 2. Filter Setup
        setupFilters(view)

        // 3. Recycler Setup
        recycler = view.findViewById(R.id.recyclerMapProjects)
        recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        loadData()
    }

    private fun setupFilters(view: View) {
        val spinStatus = view.findViewById<Spinner>(R.id.spinnerStatus)
        val spinCat = view.findViewById<Spinner>(R.id.spinnerCategory)

        val statuses = arrayOf("All Status", "Ongoing", "Finished", "Delayed")
        val categories = arrayOf("All Categories", "Infrastructure", "Building", "Utilities")

        spinStatus.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses)
        spinCat.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

        view.findViewById<TextView>(R.id.btnClear).setOnClickListener {
            spinStatus.setSelection(0)
            spinCat.setSelection(0)
            renderMap(allProjects)
        }
    }

    private fun loadData() {
        // Real-time updates so status changes reflect immediately
        db.collection("projects").addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("MapFragment", "Error loading map data", error)
                return@addSnapshotListener
            }
            if (value != null) {
                allProjects = value.toObjects(Project::class.java)
                renderMap(allProjects)
            }
        }
    }

    private fun renderMap(projects: List<Project>) {
        map.overlays.clear()

        // Reuse ProjectAdapter
        val adapter = ProjectAdapter(projects) { project ->
            if(project.location.lat != 0.0) {
                map.controller.animateTo(GeoPoint(project.location.lat, project.location.lng))
                map.controller.setZoom(18.0)
            }
        }
        recycler.adapter = adapter

        // Add Markers
        for (p in projects) {
            if (p.location.lat != 0.0 && p.location.lng != 0.0) {
                val marker = Marker(map)
                marker.position = GeoPoint(p.location.lat, p.location.lng)
                marker.title = "${p.title}\n(${p.status})"

                // --- MARKER COLOR LOGIC ---
                val icon = ContextCompat.getDrawable(requireContext(), org.osmdroid.library.R.drawable.marker_default)
                if (icon != null) {
                    val coloredIcon = icon.mutate()
                    if (p.status == "Finished") {
                        coloredIcon.setColorFilter(Color.parseColor("#2E7D32"), PorterDuff.Mode.SRC_IN)
                    } else if (p.status == "Delayed") {
                        coloredIcon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                    } else {
                        coloredIcon.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
                    }
                    marker.icon = coloredIcon
                }

                map.overlays.add(marker)
            }
        }
        map.invalidate()
    }
}