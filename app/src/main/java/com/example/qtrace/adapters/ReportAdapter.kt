package com.example.qtrace.adapters

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.DetailActivity
import com.example.qtrace.R
import com.example.qtrace.models.Project
import com.example.qtrace.models.Report
import com.google.firebase.firestore.FirebaseFirestore

class ReportAdapter(private val reportList: List<Report>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_report_title)
        val status: TextView = view.findViewById(R.id.tv_report_status)
        val category: TextView = view.findViewById(R.id.tv_report_category)
        val location: TextView = view.findViewById(R.id.tv_report_location)

        // These IDs must exist in item_report.xml
        val linkedProjectTv: TextView = view.findViewById(R.id.tv_linked_project)
        val viewProjectBtn: Button = view.findViewById(R.id.btn_view_report_project)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.title.text = report.title
        holder.category.text = report.category
        holder.location.text = report.location
        holder.status.text = report.status

        // Status Colors
        when (report.status) {
            "Resolved" -> holder.status.setTextColor(Color.GREEN)
            "Pending" -> holder.status.setTextColor(Color.parseColor("#FF9800"))
            else -> holder.status.setTextColor(Color.GRAY)
        }

        // Show Project Link if it exists
        if (report.projectId.isNotEmpty()) {
            holder.linkedProjectTv.visibility = View.VISIBLE
            holder.viewProjectBtn.visibility = View.VISIBLE
            holder.linkedProjectTv.text = "Related to: ${report.projectName}"

            holder.viewProjectBtn.setOnClickListener {
                fetchAndOpenProject(holder.itemView.context, report.projectId)
            }
        } else {
            holder.linkedProjectTv.visibility = View.GONE
            holder.viewProjectBtn.visibility = View.GONE
        }
    }

    private fun fetchAndOpenProject(context: android.content.Context, projectId: String) {
        FirebaseFirestore.getInstance().collection("projects").document(projectId).get()
            .addOnSuccessListener { document ->
                val project = document.toObject(Project::class.java)
                if (project != null) {
                    project.id = document.id
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("PROJECT_DATA", project)
                    context.startActivity(intent)
                }
            }
    }

    override fun getItemCount() = reportList.size
}