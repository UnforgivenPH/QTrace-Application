package com.example.qtrace

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Receive Serializable Data
        val project = intent.getSerializableExtra("PROJECT_DATA") as? Project

        if (project != null) {
            displayData(project)
        }
    }

    private fun displayData(project: Project) {
        findViewById<TextView>(R.id.tvDetailTitle).text = project.title
        findViewById<TextView>(R.id.tvDetailCategory).text = project.category

        val fullAddress = "${project.address.street}, ${project.address.barangay}, ${project.address.city} ${project.address.zipCode}"
        findViewById<TextView>(R.id.tvDetailAddress).text = "Address: $fullAddress"

        findViewById<TextView>(R.id.tvDetailBudget).text = "Budget: ₱${String.format("%,.2f", project.budget)}"
        findViewById<TextView>(R.id.tvDetailDates).text = "Date: ${project.dates.started} to ${project.dates.end}"
        findViewById<TextView>(R.id.tvDetailDescription).text = project.description

        // Dynamically add images for Milestones
        val milestonesContainer = findViewById<LinearLayout>(R.id.llMilestonesContainer)

        // Create a formatter
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        // Safe call (?.) to format if date exists, otherwise use "N/A"
        val startDate = project.dates.started?.let { dateFormatter.format(it) } ?: "N/A"
        val endDate = project.dates.end?.let { dateFormatter.format(it) } ?: "N/A"

        findViewById<TextView>(R.id.tvDetailDates).text = "Date: $startDate to $endDate"
        project.milestones.forEach { milestone ->
            // Create a TextView for the label
            val label = TextView(this)
            label.text = "${milestone.type} - ${milestone.dateUploaded}"
            label.setPadding(0, 16, 0, 8)
            milestonesContainer.addView(label)

            // Create an ImageView
            val imageView = ImageView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500 // Height in pixels (adjust as needed)
            )
            params.setMargins(0, 0, 0, 32)
            imageView.layoutParams = params
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            // Load image using Glide
            if (milestone.imageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(milestone.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Fallback icon
                    .into(imageView)
            }

            milestonesContainer.addView(imageView)
        }
    }
}