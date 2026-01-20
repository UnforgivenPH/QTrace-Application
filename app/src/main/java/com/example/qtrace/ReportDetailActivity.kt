package com.example.qtrace

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qtrace.models.Project
import com.example.qtrace.models.Report
import com.google.firebase.firestore.FirebaseFirestore

class ReportDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)

        val report = intent.getSerializableExtra("REPORT_DATA") as? Report ?: return

        findViewById<TextView>(R.id.tv_detail_report_title).text = report.title
        findViewById<TextView>(R.id.tv_detail_report_desc).text = report.description
        findViewById<TextView>(R.id.tv_detail_report_category).text = "Category: ${report.category}"
        findViewById<TextView>(R.id.tv_detail_report_location).text = report.location

        val statusTv = findViewById<TextView>(R.id.tv_detail_report_status)
        statusTv.text = report.status.uppercase()
        when(report.status) {
            "Resolved" -> statusTv.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
            "Pending" -> statusTv.setBackgroundColor(Color.parseColor("#FF9800")) // Orange
            else -> statusTv.setBackgroundColor(Color.GRAY)
        }

        // Handle Project Link
        val linkLayout = findViewById<LinearLayout>(R.id.layout_linked_project)
        if (report.projectId.isNotEmpty()) {
            linkLayout.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_detail_project_name).text = report.projectName

            findViewById<Button>(R.id.btn_detail_report_link).setOnClickListener {
                fetchAndOpenProject(report.projectId)
            }
        }
    }

    private fun fetchAndOpenProject(projectId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("projects").document(projectId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val project = document.toObject(Project::class.java)
                    project?.id = document.id
                    val intent = Intent(this, DetailActivity::class.java)
                    intent.putExtra("PROJECT_DATA", project)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Project not found", Toast.LENGTH_SHORT).show()
                }
            }
    }
}