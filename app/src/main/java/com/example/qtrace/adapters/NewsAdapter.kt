package com.example.qtrace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qtrace.R
import com.example.qtrace.models.News
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapter(
    private var newsList: List<News>, // Changed to 'var' to allow updates if needed
    private val onItemClick: (News) -> Unit
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgNewsThumbnail)
        // 1. Link the Fallback Bar
        val fallbackBar: View = view.findViewById(R.id.viewFallbackBar)

        val title: TextView = view.findViewById(R.id.tvNewsTitle)
        val date: TextView = view.findViewById(R.id.tvNewsDate)
        val excerpt: TextView = view.findViewById(R.id.tvNewsExcerpt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Ensure this matches your XML file name (e.g., item_news_article.xml or item_news.xml)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = newsList[position]

        holder.title.text = news.title
        holder.excerpt.text = news.content

        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        holder.date.text = if (news.datePosted != null) sdf.format(news.datePosted) else "Recent"

        // 2. 🛠️ TOGGLE LOGIC: Image vs. Bar
        if (!news.imageUrl.isNullOrEmpty()) {
            // Case A: Has Image
            holder.image.visibility = View.VISIBLE
            holder.fallbackBar.visibility = View.GONE

            Glide.with(holder.itemView.context)
                .load(news.imageUrl)
                .centerCrop()
                .placeholder(android.R.color.darker_gray) // Optional placeholder
                .into(holder.image)
        } else {
            // Case B: No Image -> Show Blue Bar
            holder.image.visibility = View.GONE
            holder.fallbackBar.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener { onItemClick(news) }
    }

    override fun getItemCount() = newsList.size

    // Helper to update list if you implement search later
    fun updateData(newArticles: List<News>) {
        newsList = newArticles
        notifyDataSetChanged()
    }
}