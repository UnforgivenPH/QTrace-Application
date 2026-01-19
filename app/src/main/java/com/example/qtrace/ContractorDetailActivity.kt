package com.example.qtrace

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.qtrace.models.Contractor

class ContractorDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contractor_detail)

        val contractor = intent.getSerializableExtra("CONTRACTOR_DATA") as? Contractor

        if (contractor != null) {
            findViewById<TextView>(R.id.tv_detail_contractor_name).text = contractor.name
            findViewById<TextView>(R.id.tv_detail_contractor_address).text = contractor.address

            val contactInfo = "👤 Representative:\n${contractor.contactPerson}\n\n📞 Phone:\n${contractor.phone}"
            findViewById<TextView>(R.id.tv_detail_contractor_contact).text = contactInfo

            val expertiseList = if (contractor.expertise.isNotEmpty()) {
                contractor.expertise.joinToString("\n• ", prefix = "• ")
            } else {
                "General Services"
            }
            findViewById<TextView>(R.id.tv_detail_contractor_expertise).text = expertiseList
        }
    }
}