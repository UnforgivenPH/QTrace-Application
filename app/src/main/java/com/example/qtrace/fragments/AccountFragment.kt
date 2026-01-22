package com.example.qtrace.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.qtrace.LoginActivity
import com.example.qtrace.R
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment(R.layout.fragment_account) {

    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", null)
        val btnAction = view.findViewById<TextView>(R.id.btnLogout)

        // Initial Load
        if (userId == null) {
            setupGuestUI(view, btnAction)
        } else {
            setupUserUI(view, btnAction, userId)
        }

        // --- Menu Clicks ---
        view.findViewById<TextView>(R.id.btnMyReports).setOnClickListener {
            // Check session dynamically in case they just logged out
            val currentId = sharedPref.getString("USER_ID", null)
            if (currentId == null) Toast.makeText(context, "Login required", Toast.LENGTH_SHORT).show()
            else Toast.makeText(context, "History feature coming soon", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<TextView>(R.id.btnPersonalInfo).setOnClickListener {
            val currentId = sharedPref.getString("USER_ID", null)
            if (currentId == null) Toast.makeText(context, "Login required", Toast.LENGTH_SHORT).show()
            else Toast.makeText(context, "Edit Profile feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupGuestUI(view: View, btnAction: TextView) {
        // 1. Reset Text to Guest Mode
        view.findViewById<TextView>(R.id.tvAccountName).text = "Guest User"
        view.findViewById<TextView>(R.id.tvAccountAddress).text = "Sign in to access features"
        view.findViewById<TextView>(R.id.tvAccountId).text = ""

        // 2. Style Button as "Log In"
        btnAction.text = "Log In"
        btnAction.setTextColor(Color.parseColor("#0D6EFD")) // Blue
        btnAction.setBackgroundColor(Color.parseColor("#E3F2FD")) // Light Blue BG

        // 3. Set Action -> Go to Login Activity
        btnAction.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    private fun setupUserUI(view: View, btnAction: TextView, userId: String) {
        // 1. Style Button as "Log Out"
        btnAction.text = "Log Out"
        btnAction.setTextColor(Color.parseColor("#DC3545")) // Red
        btnAction.setBackgroundColor(Color.parseColor("#FFEBEE")) // Light Red BG

        // 2. Set Action -> Logout & Refresh Local UI
        btnAction.setOnClickListener {
            // A. Clear Session
            val sharedPref = requireContext().getSharedPreferences("AppSession", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                apply()
            }
            Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()

            // B. ⚡ INSTANTLY SWITCH TO GUEST UI (Stay on this screen) ⚡
            setupGuestUI(view, btnAction)
        }

        // 3. Load Profile Data
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val first = doc.getString("fullName.first") ?: ""
                    val last = doc.getString("fullName.last") ?: ""
                    val address = doc.getString("details.address") ?: "No Address"
                    val qcId = doc.getString("qcId") ?: "N/A"

                    view.findViewById<TextView>(R.id.tvAccountName).text = "$first $last"
                    view.findViewById<TextView>(R.id.tvAccountAddress).text = address
                    view.findViewById<TextView>(R.id.tvAccountId).text = "QC ID: $qcId"
                }
            }
    }
}