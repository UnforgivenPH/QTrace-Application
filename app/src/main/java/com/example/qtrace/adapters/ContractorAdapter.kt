package com.example.qtrace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qtrace.R
import com.example.qtrace.models.Contractor

class ContractorAdapter(
    private val contractors: List<Contractor>,
    private val onItemClick: (Contractor) -> Unit
) : RecyclerView.Adapter<ContractorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo: ImageView = view.findViewById(R.id.imgContractorLogo)
        val name: TextView = view.findViewById(R.id.tvContractorName)
        val expertise: TextView = view.findViewById(R.id.tvContractorExpertise)
        val activeCount: TextView = view.findViewById(R.id.tvActiveCount)
        val completedCount: TextView = view.findViewById(R.id.tvCompletedCount)
        val btnProfile: TextView = view.findViewById(R.id.btnViewProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contractor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contractor = contractors[position]

        // 1. Set Text Data
        holder.name.text = contractor.name

        // Handle expertise list safely (avoids crash if list is null)
        holder.expertise.text = contractor.expertise?.joinToString(", ") ?: "General"

        // Set the counters directly from the database model
        holder.activeCount.text = contractor.activeProjects.toString()
        holder.completedCount.text = contractor.completedProjects.toString()

        // 2. Load Logo with Safety Check
        // We ensure 'logo' isn't null and the path is valid before trying to load
        if (contractor.logo != null && contractor.logo.path.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(contractor.logo.path)
                .placeholder(R.drawable.ic_launcher_foreground) // Shows this while loading
                .error(R.drawable.ic_launcher_foreground)       // Shows this if URL is broken
                .into(holder.logo)
        } else {
            // Explicitly set placeholder if no logo exists (handles recycling views correctly)
            holder.logo.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // 3. Click Listeners
        // Clicking the whole card OR the "View Profile" button does the same thing
        holder.itemView.setOnClickListener { onItemClick(contractor) }
        holder.btnProfile.setOnClickListener { onItemClick(contractor) }
    }

    override fun getItemCount() = contractors.size
}