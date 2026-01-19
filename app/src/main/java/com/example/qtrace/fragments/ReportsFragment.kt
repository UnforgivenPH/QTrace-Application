package com.example.qtrace.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.adapters.ReportAdapter
import com.example.qtrace.models.Report
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ReportsFragment : Fragment(R.layout.fragment_reports) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private val reportList = mutableListOf<Report>()
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_reports)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ReportAdapter(reportList)
        recyclerView.adapter = adapter

        // Load Data
        fetchReports()
    }

    private fun fetchReports() {
        db.collection("reports")
            //.orderBy("dateSubmitted", Query.Direction.DESCENDING) // Uncomment if you want newest first
            .get()
            .addOnSuccessListener { result ->
                reportList.clear()
                for (document in result) {
                    try {
                        val report = document.toObject(Report::class.java)
                        report.id = document.id
                        reportList.add(report)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                context?.let {
                    Toast.makeText(it, "Failed to load reports", Toast.LENGTH_SHORT).show()
                }
            }
    }
}