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
    private val newsList: List<News>,
    private val onItemClick: (News) -> Unit
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgNewsThumbnail)
        val title: TextView = view.findViewById(R.id.tvNewsTitle)
        val date: TextView = view.findViewById(R.id.tvNewsDate)
        val excerpt: TextView = view.findViewById(R.id.tvNewsExcerpt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = newsList[position]

        holder.title.text = news.title
        holder.excerpt.text = news.content

        // 🛠️ FIX: Use 'datePosted' instead of 'date'
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        holder.date.text = if(news.datePosted != null) sdf.format(news.datePosted) else "Recent"

        // 🛠️ FIX: Use 'imageUrl' (from your model)
        if (!news.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(news.imageUrl)
                .centerCrop()
                .into(holder.image)
        }

        holder.itemView.setOnClickListener { onItemClick(news) }
    }

    override fun getItemCount() = newsList.size
}