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

class ProjectAdapter(
    private val projects: List<Project>,
    private val onItemClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvProjectTitle)
        val location: TextView = view.findViewById(R.id.tvLocation)
        val budget: TextView = view.findViewById(R.id.tvBudget)
        val date: TextView = view.findViewById(R.id.tvDate)
        val btnView: View = view.findViewById(R.id.btnViewDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Uses the item_project.xml we created earlier
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]

        holder.title.text = project.title
        holder.location.text = "${project.address.street}, ${project.address.city}"

        // Format Budget
        val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
        holder.budget.text = format.format(project.budget)

        // Simple Date
        holder.date.text = "Ongoing" // You can parse dates here if needed

        // Click Listeners
        holder.itemView.setOnClickListener { onItemClick(project) }
        holder.btnView.setOnClickListener { onItemClick(project) }
    }

    override fun getItemCount() = projects.size
}