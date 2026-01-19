package com.example.qtrace.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.qtrace.R
import com.example.qtrace.ReportFormActivity // We will create this next

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Link the "Report Now" button
        val reportBtn = view.findViewById<Button>(R.id.btn_report_issue)

        reportBtn?.setOnClickListener {
            val intent = Intent(requireContext(), ReportFormActivity::class.java)
            startActivity(intent)
        }
    }
}