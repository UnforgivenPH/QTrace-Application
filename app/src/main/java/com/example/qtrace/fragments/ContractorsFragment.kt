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
import com.example.qtrace.ContractorDetailActivity
import com.example.qtrace.R
import com.example.qtrace.adapters.ContractorAdapter
import com.example.qtrace.models.Contractor
import com.google.firebase.firestore.FirebaseFirestore

class ContractorsFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private var allContractors = listOf<Contractor>() // Store full list here

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contractors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerContractors)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // 1. Load Data
        loadData()

        // 2. Setup Search Listener
        val searchBox = view.findViewById<EditText>(R.id.etSearchContractor)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadData() {
        db.collection("contractors").get().addOnSuccessListener { result ->
            allContractors = result.toObjects(Contractor::class.java)
            renderList(allContractors)
        }
    }

    private fun filterList(query: String) {
        val filtered = allContractors.filter {
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