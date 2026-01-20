package com.example.qtrace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qtrace.R
import com.example.qtrace.models.Milestone // Import your Milestone model

class MilestoneAdapter(private val milestones: List<Milestone>) :
    RecyclerView.Adapter<MilestoneAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgMilestone)
        val type: TextView = view.findViewById(R.id.tvMilestoneType)
        val date: TextView = view.findViewById(R.id.tvMilestoneDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_milestone, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = milestones[position]

        // 1. Bind Data directly from the Milestone object
        holder.type.text = item.type ?: "Update"

        // 2. Format Date (Simple check to keep it short)
        val dateRaw = item.dateUploaded ?: ""
        holder.date.text = if(dateRaw.length > 10) dateRaw.substring(0, 10) else dateRaw

        // 3. Load Image
        if (!item.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .centerCrop()
                .into(holder.image)
        }
    }

    override fun getItemCount() = milestones.size
}