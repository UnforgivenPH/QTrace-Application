package com.example.qtrace.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.adapters.NewsAdapter
import com.example.qtrace.models.News
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NewsFragment : Fragment(R.layout.fragment_news) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private val newsList = mutableListOf<News>()
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_news)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = NewsAdapter(newsList)
        recyclerView.adapter = adapter

        fetchNews()
    }

    private fun fetchNews() {
        db.collection("news")
            .orderBy("datePosted", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                newsList.clear()
                for (document in result) {
                    try {
                        val newsItem = document.toObject(News::class.java)
                        newsItem.id = document.id
                        newsList.add(newsItem)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                context?.let {
                    Toast.makeText(it, "Failed to load news", Toast.LENGTH_SHORT).show()
                }
            }
    }
}