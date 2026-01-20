package com.example.qtrace.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.NewsDetailActivity
import com.example.qtrace.R
import com.example.qtrace.adapters.NewsAdapter
import com.example.qtrace.models.News
import com.google.firebase.firestore.FirebaseFirestore

class NewsFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private var allNews = listOf<News>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerNews)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        loadData()

        val searchBox = view.findViewById<EditText>(R.id.etSearchNews)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadData() {
        db.collection("articles").get().addOnSuccessListener { result ->
            allNews = result.toObjects(News::class.java)
            renderList(allNews)
        }
    }

    private fun filterList(query: String) {
        val filtered = allNews.filter {
            // Check title (and safely check content if it exists)
            val titleMatch = it.title?.contains(query, ignoreCase = true) == true
            val contentMatch = it.content?.contains(query, ignoreCase = true) == true
            titleMatch || contentMatch
        }
        renderList(filtered)
    }

    private fun renderList(list: List<News>) {
        val adapter = NewsAdapter(list) { newsItem ->
            val intent = Intent(requireContext(), NewsDetailActivity::class.java)
            intent.putExtra("NEWS_DATA", newsItem)
            startActivity(intent)
        }
        recycler.adapter = adapter
    }
}