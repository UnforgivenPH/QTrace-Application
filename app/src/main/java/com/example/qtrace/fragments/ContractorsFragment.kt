package com.example.qtrace.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.adapters.ContractorAdapter
import com.example.qtrace.models.Contractor
import com.google.firebase.firestore.FirebaseFirestore

class ContractorsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var adapter: ContractorAdapter
    private val contractorList = ArrayList<Contractor>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contractors, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_contractors)
        emptyStateText = view.findViewById(R.id.tv_empty_state)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ContractorAdapter(contractorList)
        recyclerView.adapter = adapter

        fetchContractors()

        return view
    }

    private fun fetchContractors() {
        db.collection("contractors")
            .get()
            .addOnSuccessListener { result ->
                // Check if fragment is still active before updating UI
                if (!isAdded) return@addOnSuccessListener

                contractorList.clear()
                for (document in result) {
                    try {
                        val contractor = document.toObject(Contractor::class.java)
                        contractor.id = document.id
                        contractorList.add(contractor)
                    } catch (e: Exception) {
                        e.printStackTrace() // Log error but DON'T CRASH
                    }
                }

                adapter.notifyDataSetChanged()

                // Toggle Empty State
                if (contractorList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyStateText.visibility = View.VISIBLE
                    emptyStateText.text = "No contractors found."
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyStateText.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                if (isAdded) {
                    Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}