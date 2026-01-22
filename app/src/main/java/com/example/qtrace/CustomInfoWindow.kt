package com.example.qtrace

import android.content.Intent
import android.graphics.Color
import android.widget.Button
import android.widget.TextView
import com.example.qtrace.models.Project
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.Marker

class CustomInfoWindow(mapView: MapView) : InfoWindow(R.layout.layout_info_window, mapView) {

    var selectedProject: Project? = null

    override fun onOpen(item: Any?) {
        val marker = item as Marker

        val tvTitle = mView.findViewById<TextView>(R.id.tvTitle)
        val tvStatus = mView.findViewById<TextView>(R.id.tvStatus)
        val tvContractor = mView.findViewById<TextView>(R.id.tvContractor)
        val btnDetails = mView.findViewById<Button>(R.id.btnDetails)

        tvTitle.text = marker.title
        tvContractor.text = marker.snippet
        tvStatus.text = marker.subDescription

        // 🎨 DYNAMIC TEXT COLOR (Matches Marker Color)
        val statusText = marker.subDescription ?: ""
        val statusColor = when {
            statusText.equals("Finished", ignoreCase = true) -> Color.parseColor("#198754") // Green
            statusText.equals("Delayed", ignoreCase = true) -> Color.parseColor("#DC3545")  // Red
            else -> Color.parseColor("#0D6EFD") // Blue
        }
        tvStatus.setTextColor(statusColor)

        btnDetails.setOnClickListener {
            selectedProject?.let { project ->
                val context = mView.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("PROJECT_DATA", project)
                context.startActivity(intent)
                close()
            }
        }
    }

    override fun onClose() {
        // No cleanup needed
    }
}