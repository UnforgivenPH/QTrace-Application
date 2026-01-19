package com.example.qtrace

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ReportFormActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_form)

        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        btnSubmit.setOnClickListener {
            submitReport()
        }
    }

    private fun submitReport() {
        val title = findViewById<EditText>(R.id.et_title).text.toString()
        val location = findViewById<EditText>(R.id.et_location).text.toString()
        val description = findViewById<EditText>(R.id.et_description).text.toString()
        val category = findViewById<Spinner>(R.id.sp_category).selectedItem.toString()

        if (title.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a simple map for the report
        val reportData = hashMapOf(
            "title" to title,
            "location" to location,
            "description" to description,
            "category" to category,
            "status" to "Pending",
            "dateSubmitted" to Date()
        )

        db.collection("reports")
            .add(reportData)
            .addOnSuccessListener {
                Toast.makeText(this, "Report Submitted!", Toast.LENGTH_LONG).show()
                finish() // Close activity
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error submitting report", Toast.LENGTH_SHORT).show()
            }
    }
}