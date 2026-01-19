package com.example.qtrace

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide // Import Glide
import com.example.qtrace.models.Contractor

class ContractorDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contractor_detail)

        val contractor = intent.getSerializableExtra("CONTRACTOR_DATA") as? Contractor

        if (contractor != null) {
            findViewById<TextView>(R.id.tv_detail_contractor_name).text = contractor.name
            findViewById<TextView>(R.id.tv_detail_contractor_address).text = contractor.address

            // 1. Show Contact Person & Phone
            val contactInfo = "👤 Representative:\n${contractor.contactPerson}\n\n📞 Phone:\n${contractor.phone}"
            findViewById<TextView>(R.id.tv_detail_contractor_contact).text = contactInfo

            // 2. ✅ NEW: Show Email
            val emailText = if (contractor.email.isNotEmpty()) contractor.email else "No email provided"
            findViewById<TextView>(R.id.tv_detail_contractor_email).text = "✉️ Email:\n$emailText"

            // 3. Show Expertise
            // Note: In Contractor.kt we mapped 'experience' from Firebase to 'expertise' in Kotlin
            val expertiseList = if (contractor.expertise.isNotEmpty()) {
                contractor.expertise.joinToString("\n• ", prefix = "• ")
            } else {
                "General Services"
            }
            findViewById<TextView>(R.id.tv_detail_contractor_expertise).text = expertiseList

            // 4. ✅ NEW: Load Logo Image using Glide
            val logoImg = findViewById<ImageView>(R.id.img_contractor_logo)
            if (contractor.logo.path.isNotEmpty()) { // Use .path here
                Glide.with(this).load(contractor.logo.path).into(logoImg)
            }
        }
    }
}