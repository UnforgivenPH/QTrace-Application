package com.example.qtrace.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.adapters.MapProjectAdapter // You'll create this adapter similar to others
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
        map.controller.setCenter(GeoPoint(14.6760, 121.0437)) // QC Center

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

        // Simple Adapters for Filters
        val statuses = arrayOf("All Status", "Ongoing", "Finished", "Delayed")
        val categories = arrayOf("All Categories", "Infrastructure", "Building", "Utilities")

        spinStatus.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses)
        spinCat.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

        view.findViewById<TextView>(R.id.btnClear).setOnClickListener {
            spinStatus.setSelection(0)
            spinCat.setSelection(0)
            renderMap(allProjects)
        }

        // Note: You should add onItemSelectedListeners here to filter 'allProjects'
    }

    private fun loadData() {
        db.collection("projects").get().addOnSuccessListener { result ->
            allProjects = result.toObjects(Project::class.java)
            renderMap(allProjects)
        }
    }

    private fun renderMap(projects: List<Project>) {
        map.overlays.clear()

        // Update Bottom List
        val adapter = MapProjectAdapter(projects) { project ->
            // On Card Click -> Zoom to Map
            if(project.location != null) {
                map.controller.animateTo(GeoPoint(project.location!!.lat, project.location!!.lng))
                map.controller.setZoom(16.0)
            }
        }
        recycler.adapter = adapter

        // Add Markers
        for (p in projects) {
            if (p.location != null && p.location!!.lat != 0.0) {
                val marker = Marker(map)
                marker.position = GeoPoint(p.location!!.lat, p.location!!.lng)
                marker.title = p.title

                // Set Icon Color based on status (requires custom drawables, default is blue)
                // marker.icon = resources.getDrawable(...)

                marker.setOnMarkerClickListener { m, _ ->
                    m.showInfoWindow()
                    // Optional: Scroll bottom list to this project
                    return@setOnMarkerClickListener true
                }
                map.overlays.add(marker)
            }
        }
        map.invalidate()
    }
}