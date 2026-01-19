package com.example.qtrace.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qtrace.NewsDetailActivity // Import the new activity
import com.example.qtrace.R
import com.example.qtrace.models.News
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapter(private val newsList: List<News>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.img_news_banner)
        val title: TextView = view.findViewById(R.id.tv_news_title)
        val date: TextView = view.findViewById(R.id.tv_news_date)
        val content: TextView = view.findViewById(R.id.tv_news_content)
        // We removed the button from the row layout because it's now inside the new screen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = newsList[position]
        val context = holder.itemView.context

        holder.title.text = news.title
        holder.content.text = news.content

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateString = news.datePosted?.let { dateFormat.format(it) } ?: "Just Now"
        holder.date.text = "$dateString • ${news.author}"

        if (news.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(news.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.image)
        }

        // ✅ NEW: Click row to open the new NewsDetailActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra("NEWS_DATA", news)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = newsList.size
}