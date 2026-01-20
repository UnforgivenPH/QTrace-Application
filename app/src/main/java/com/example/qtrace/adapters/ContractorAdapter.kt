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

        holder.name.text = contractor.name
        holder.expertise.text = contractor.expertise.joinToString(", ")

        // Fix 1: These references now exist in the model
        holder.activeCount.text = contractor.activeProjects.toString()
        holder.completedCount.text = contractor.completedProjects.toString()

        // Fix 2: Handle the LogoData object correctly
        // We check if the 'path' inside the logo object is not empty
        if (contractor.logo.path.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(contractor.logo.path) // Load the path string
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.logo)
        }

        holder.itemView.setOnClickListener { onItemClick(contractor) }
        holder.btnProfile.setOnClickListener { onItemClick(contractor) }
    }

    override fun getItemCount() = contractors.size
}