package com.example.qtrace.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qtrace.DetailActivity
import com.example.qtrace.R
import com.example.qtrace.models.News
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapter(private val newsList: List<News>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Ensure these IDs exist in your item_news.xml
        val title: TextView = view.findViewById(R.id.tvNewsTitle)
        val content: TextView = view.findViewById(R.id.tvNewsContent)
        val date: TextView = view.findViewById(R.id.tvNewsDate)
        val image: ImageView = view.findViewById(R.id.imgNewsHeader)
        val btnViewProject: Button = view.findViewById(R.id.btnViewRelatedProject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]

        // 1. Text Data
        holder.title.text = news.title
        holder.content.text = news.content

        // Format Date
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        holder.date.text = if (news.datePosted != null) sdf.format(news.datePosted) else "Just now"

        // 2. Image Handling (Fix for empty URL in your screenshot)
        if (news.imageUrl.isNotEmpty()) {
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(news.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(holder.image)
        } else {
            // Hide image view if no URL exists (like in your screenshot)
            holder.image.visibility = View.GONE
        }

        // 3. "View Project" Button Logic
        if (news.projectId.isNotEmpty()) {
            holder.btnViewProject.visibility = View.VISIBLE
            holder.btnViewProject.setOnClickListener {
                fetchAndOpenProject(holder.itemView.context, news.projectId)
            }
        } else {
            holder.btnViewProject.visibility = View.GONE
        }
    }

    private fun fetchAndOpenProject(context: android.content.Context, projectId: String) {
        FirebaseFirestore.getInstance().collection("projects").document(projectId).get()
            .addOnSuccessListener { document ->
                try {
                    val project = document.toObject(Project::class.java)
                    if (project != null) {
                        project.id = document.id
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("PROJECT_DATA", project)
                        context.startActivity(intent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    override fun getItemCount() = newsList.size
}