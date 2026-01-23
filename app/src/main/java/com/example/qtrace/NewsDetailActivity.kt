package com.example.qtrace

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.qtrace.models.News
import com.example.qtrace.models.Project
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale


class NewsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 🛠️ Ensure this matches your file name: activity_news_detail.xml
        setContentView(R.layout.activity_news_detail)

        val news = intent.getSerializableExtra("NEWS_DATA") as? News

        if (news == null) {
            finish()
            return
        }

        // 🛠️ TOP NAV: Link the custom back button ID from your XML
        findViewById<View>(R.id.btnBackNews).setOnClickListener {
            finish()
        }

        // Bind Views
        val imgBanner: ImageView = findViewById(R.id.img_detail_banner)
        val tvTitle: TextView = findViewById(R.id.tv_detail_title)
        val tvDate: TextView = findViewById(R.id.tv_detail_date)
        val tvContent: TextView = findViewById(R.id.tv_detail_content)
        val btnLink: Button = findViewById(R.id.btn_detail_project_link)

        // Set Data
        tvTitle.text = news.title
        tvContent.text = news.content

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateString = news.datePosted?.let { dateFormat.format(it) } ?: "Recently"
        tvDate.text = "Posted on $dateString • ${news.author}"

        if (news.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(news.imageUrl)
                .placeholder(R.color.image_placeholder) // Use your color scheme
                .centerCrop()
                .into(imgBanner)
        }

        // Handle "View Related Project" Button
        if (news.projectId.isNotEmpty()) {
            btnLink.visibility = View.VISIBLE
            btnLink.setOnClickListener {
                fetchAndOpenProject(news.projectId)
            }
        } else {
            btnLink.visibility = View.GONE
        }
    }

    private fun fetchAndOpenProject(projectId: String) {
        val db = FirebaseFirestore.getInstance()
        Toast.makeText(this, "Loading Project...", Toast.LENGTH_SHORT).show()

        db.collection("projects").document(projectId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val project = document.toObject(Project::class.java)
                    // Ensure the ID is set for further actions in DetailActivity
                    project?.id = document.id

                    val intent = Intent(this, DetailActivity::class.java)
                    intent.putExtra("PROJECT_DATA", project)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Project no longer available", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}