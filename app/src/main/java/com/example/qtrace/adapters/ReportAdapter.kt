package com.example.qtrace.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.models.Report

class ReportAdapter(private val reportList: List<Report>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_report_title)
        val status: TextView = view.findViewById(R.id.tv_report_status)
        val category: TextView = view.findViewById(R.id.tv_report_category)
        val location: TextView = view.findViewById(R.id.tv_report_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]

        holder.title.text = report.title
        holder.category.text = report.category
        holder.location.text = report.location
        holder.status.text = report.status

        // Status Color Coding
        when (report.status) {
            "Resolved" -> holder.status.setTextColor(Color.GREEN)
            "Pending" -> holder.status.setTextColor(Color.parseColor("#FF9800")) // Orange
            else -> holder.status.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount() = reportList.size
}