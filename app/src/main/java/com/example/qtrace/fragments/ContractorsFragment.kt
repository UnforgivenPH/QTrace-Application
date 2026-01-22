package com.example.qtrace.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.ContractorDetailActivity
import com.example.qtrace.R
import com.example.qtrace.adapters.ContractorAdapter
import com.example.qtrace.models.Contractor
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore

class ContractorsFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private var fullContractorList = listOf<Contractor>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contractors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerContractors)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        loadDataWithCounts()

        val searchBox = view.findViewById<EditText>(R.id.etSearchContractor)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterList(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadDataWithCounts() {
        db.collection("contractors").get().addOnSuccessListener { contractorSnap ->
            val contractors = contractorSnap.toObjects(Contractor::class.java)

            // Ensure IDs are set from the document snapshot
            contractors.forEachIndexed { index, contractor ->
                contractor.id = contractorSnap.documents[index].id
            }

            db.collection("projects").get().addOnSuccessListener { projectSnap ->
                val projects = projectSnap.toObjects(Project::class.java)

                for (contractor in contractors) {
                    var active = 0
                    var completed = 0

                    for (project in projects) {
                        // 🛠️ FIX: Match by ID OR Name (Handles Web Admin mismatch)
                        val isMatch = (project.contractorId == contractor.id) ||
                                (project.contractor == contractor.name)

                        if (isMatch) {
                            // ✅ Tag "Delayed" as Active here
                            if (project.status == "Ongoing" || project.status == "Delayed") {
                                active++
                            } else if (project.status == "Finished") {
                                completed++
                            }
                        }
                    }

                    contractor.activeProjects = active
                    contractor.completedProjects = completed
                }

                fullContractorList = contractors
                renderList(fullContractorList)
            }
        }.addOnFailureListener {
            Log.e("ContractorsFragment", "Error loading data", it)
        }
    }

    private fun filterList(query: String) {
        val filtered = fullContractorList.filter {
            it.name.contains(query, ignoreCase = true) ||
                    (it.expertise?.any { exp -> exp.contains(query, ignoreCase = true) } == true)
        }
        renderList(filtered)
    }

    private fun renderList(list: List<Contractor>) {
        val adapter = ContractorAdapter(list) { contractor ->
            val intent = Intent(requireContext(), ContractorDetailActivity::class.java)
            intent.putExtra("CONTRACTOR_DATA", contractor)
            startActivity(intent)
        }
        recycler.adapter = adapter
    }
}