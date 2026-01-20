package com.example.qtrace.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.DetailActivity
import com.example.qtrace.R
import com.example.qtrace.adapters.ProjectAdapter // Your standard vertical list adapter
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore

class ProjectFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private var allProjects = listOf<Project>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Ensure this layout is the CLEAN list version (no map view inside)
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerProjects)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        loadProjects()

        // Simple Search Logic (Optional)
        view.findViewById<EditText>(R.id.etSearchProject).setOnEditorActionListener { v, _, _ ->
            filterList(v.text.toString())
            true
        }
    }

    private fun loadProjects() {
        db.collection("projects").get().addOnSuccessListener { result ->
            allProjects = result.toObjects(Project::class.java)
            renderList(allProjects)
        }
    }

    private fun renderList(list: List<Project>) {
        val adapter = ProjectAdapter(list) { project ->
            // Navigate to DetailActivity
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