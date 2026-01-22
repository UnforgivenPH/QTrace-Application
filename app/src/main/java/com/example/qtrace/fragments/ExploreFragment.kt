package com.example.qtrace.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.CustomInfoWindow
import com.example.qtrace.DetailActivity
import com.example.qtrace.R
import com.example.qtrace.adapters.ProjectAdapter
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class ExploreFragment : Fragment() {

    private lateinit var map: MapView
    private lateinit var recycler: RecyclerView
    private lateinit var etSearch: EditText

    private val db = FirebaseFirestore.getInstance()
    private var allProjects = listOf<Project>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setup Map
        map = view.findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(13.0)
        map.controller.setCenter(GeoPoint(14.6760, 121.0437))

        // 🛠️ CLICK TO CLOSE: Add MapEventsOverlay
        // This detects clicks on the map background (not on markers)
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                // Close all open popups when map is clicked
                InfoWindow.closeAllInfoWindowsOn(map)
                return true
            }
            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }
        val eventsOverlay = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(0, eventsOverlay) // Add at index 0 (bottom layer)

        // 2. Setup List
        recycler = view.findViewById(R.id.recyclerProjects)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // 3. Setup Search
        etSearch = view.findViewById(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterData(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loadProjects()
    }

    private fun loadProjects() {
        db.collection("projects").addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener
            if (value != null) {
                allProjects = value.toObjects(Project::class.java)
                updateUI(allProjects)
            }
        }
    }

    private fun updateUI(projects: List<Project>) {
        val adapter = ProjectAdapter(projects) { project ->
            if (project.location.lat != 0.0) {
                map.controller.animateTo(GeoPoint(project.location.lat, project.location.lng))
                map.controller.setZoom(18.0)
            }
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("PROJECT_DATA", project)
            startActivity(intent)
        }
        recycler.adapter = adapter

        // Clear existing markers (KEEP the MapEventsOverlay at index 0)
        // We iterate backwards to remove only Markers, preserving the event overlay
        val overlaysToRemove = map.overlays.filterIsInstance<Marker>()
        map.overlays.removeAll(overlaysToRemove)

        for (p in projects) {
            if (p.location.lat != 0.0 && p.location.lng != 0.0) {
                val marker = Marker(map)
                marker.position = GeoPoint(p.location.lat, p.location.lng)

                marker.title = p.title
                marker.snippet = p.contractor
                marker.subDescription = p.status

                val infoWindow = CustomInfoWindow(map)
                infoWindow.selectedProject = p
                marker.infoWindow = infoWindow

                // Color Logic
                val icon = ContextCompat.getDrawable(requireContext(), org.osmdroid.library.R.drawable.marker_default)?.mutate()
                val color = when {
                    p.status.equals("Finished", ignoreCase = true) -> Color.parseColor("#198754")
                    p.status.equals("Delayed", ignoreCase = true) -> Color.parseColor("#DC3545")
                    else -> Color.parseColor("#0D6EFD")
                }
                icon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                marker.icon = icon

                marker.setOnMarkerClickListener { m, _ ->
                    // Close others before opening new one
                    InfoWindow.closeAllInfoWindowsOn(map)
                    m.showInfoWindow()
                    true
                }

                map.overlays.add(marker)
            }
        }
        map.invalidate()
    }

    private fun filterData(query: String) {
        val filtered = allProjects.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.contractor.contains(query, ignoreCase = true) ||
                    it.address.city.contains(query, ignoreCase = true)
        }
        updateUI(filtered)
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