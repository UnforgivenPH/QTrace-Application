package com.example.qtrace.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.adapters.ContractorAdapter
import com.example.qtrace.models.Contractor
import com.google.firebase.firestore.FirebaseFirestore

class ContractorsFragment : Fragment(R.layout.fragment_contractors) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContractorAdapter
    private val contractorList = mutableListOf<Contractor>()
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_contractors)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ContractorAdapter(contractorList)
        recyclerView.adapter = adapter

        fetchContractors()
    }

    private fun fetchContractors() {
        db.collection("contractors")
            .get()
            .addOnSuccessListener { result ->
                contractorList.clear()
                for (document in result) {
                    try {
                        val contractor = document.toObject(Contractor::class.java)
                        contractor.id = document.id
                        contractorList.add(contractor)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                context?.let {
                    Toast.makeText(it, "Failed to load contractors", Toast.LENGTH_SHORT).show()
                }
            }
    }
}