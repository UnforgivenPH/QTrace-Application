package com.example.qtrace.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.DetailActivity
import com.example.qtrace.R
import com.example.qtrace.adapters.ProjectAdapter
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore

class ProjectFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private var allProjects = listOf<Project>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerProjects)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Load projects immediately with real-time listening
        loadProjects()

        view.findViewById<EditText>(R.id.etSearchProject).setOnEditorActionListener { v, _, _ ->
            filterList(v.text.toString())
            true
        }
    }

    private fun loadProjects() {
        db.collection("projects").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("ProjectFragment", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                allProjects = snapshots.toObjects(Project::class.java)
                renderList(allProjects)
            }
        }
    }

    private fun renderList(list: List<Project>) {
        val adapter = ProjectAdapter(list) { project ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("PROJECT_DATA", project)
            startActivity(intent)
        }
        recycler.adapter = adapter
    }

    private fun filterList(query: String) {
        val filtered = allProjects.filter {
            it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
        renderList(filtered)
    }
}