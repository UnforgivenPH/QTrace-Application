package com.example.qtrace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.models.Project
import java.text.NumberFormat
import java.util.Locale

class MapProjectAdapter(
    private val projects: List<Project>,
    private val onProjectClick: (Project) -> Unit
) : RecyclerView.Adapter<MapProjectAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvMapTitle)
        val location: TextView = view.findViewById(R.id.tvMapLocation)
        val budget: TextView = view.findViewById(R.id.tvMapBudget)
        val status: TextView = view.findViewById(R.id.tvMapStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_map_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]

        holder.title.text = project.title
        holder.location.text = "${project.address.street}, ${project.address.city}"
        holder.status.text = project.status

        // Format Budget (e.g., ₱20,000,000)
        val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
        holder.budget.text = format.format(project.budget)

        // Status Color Logic
        val context = holder.itemView.context
        val bgDrawable = context.getDrawable(R.drawable.bg_status_pill)?.mutate()

        // You can tint programmatically if you don't want multiple drawables
        when (project.status) {
            "Ongoing" -> bgDrawable?.setTint(context.getColor(android.R.color.holo_blue_dark))
            "Finished" -> bgDrawable?.setTint(context.getColor(android.R.color.holo_green_dark))
            "Delayed" -> bgDrawable?.setTint(context.getColor(android.R.color.holo_red_dark))
        }
        holder.status.background = bgDrawable

        // Click Listener -> Zoom on Map
        holder.itemView.setOnClickListener { onProjectClick(project) }
    }

    override fun getItemCount() = projects.size
}