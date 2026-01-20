package com.example.qtrace.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.adapters.NewsAdapter
import com.example.qtrace.models.News
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var adapter: NewsAdapter
    private val newsList = ArrayList<News>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Inflate the layout (Ensure fragment_news.xml is updated!)
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        // 2. Find Views (using the IDs we added to XML)
        recyclerView = view.findViewById(R.id.recycler_view_news)
        emptyStateText = view.findViewById(R.id.tv_empty_state)

        // 3. Setup List
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NewsAdapter(newsList)
        recyclerView.adapter = adapter

        // 4. Load Data
        fetchNews()

        return view
    }

    private fun fetchNews() {
        // ✅ CORRECT COLLECTION: "articles" (from your screenshot)
        db.collection("articles")
            .whereEqualTo("article_status", "Published") // Matches screenshot status
            .orderBy("article_created_at", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                newsList.clear()
                for (document in result) {
                    try {
                        val newsItem = document.toObject(News::class.java)
                        newsItem.id = document.id
                        newsList.add(newsItem)
                    } catch (e: Exception) {
                        Log.e("NewsFragment", "Error parsing: ${document.id}", e)
                    }
                }
                adapter.notifyDataSetChanged()

                // Toggle Empty State
                if (newsList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyStateText.visibility = View.VISIBLE
                    emptyStateText.text = "No published articles found."
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyStateText.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}