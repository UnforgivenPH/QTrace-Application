package com.example.qtrace.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.models.Project
import java.text.NumberFormat
import java.util.Locale

class ProjectAdapter(
    private val projects: List<Project>,
    private val onItemClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvProjectTitle)
        val location: TextView = view.findViewById(R.id.tvLocation)
        val budget: TextView = view.findViewById(R.id.tvBudget)
        val status: TextView = view.findViewById(R.id.tvDate) // Using this for Status pill
        val btnView: View = view.findViewById(R.id.btnViewDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]

        holder.title.text = project.title

        // Safety check for empty address
        val street = if(project.address.street.isNotEmpty()) project.address.street else "No Street"
        val city = if(project.address.city.isNotEmpty()) project.address.city else "Quezon City"
        holder.location.text = "$street, $city"

        // Format Budget
        try {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
            holder.budget.text = format.format(project.budget)
        } catch (e: Exception) {
            holder.budget.text = "₱${project.budget}"
        }

        // --- STATUS COLOR LOGIC ---
        holder.status.text = project.status ?: "Ongoing"

        // Reset background to avoid recycling issues
        holder.status.setBackgroundResource(0)

        if (project.status == "Finished") {
            // Dark Green Text, Light Green Background
            holder.status.setTextColor(Color.parseColor("#198754"))

        } else if (project.status == "Delayed") {
            // Red Text, Light Red Background
            holder.status.setTextColor(Color.parseColor("#DC3545"))

        } else {
            // Blue Text, Light Blue Background (Default for Ongoing)
            holder.status.setTextColor(Color.parseColor("#0D6EFD"))

        }

        // Add padding since we added a background color
        holder.status.setPadding(16, 8, 16, 8)

        holder.itemView.setOnClickListener { onItemClick(project) }
        holder.btnView.setOnClickListener { onItemClick(project) }
    }

    override fun getItemCount() = projects.size
}