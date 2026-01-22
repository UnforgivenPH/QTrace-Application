package com.example.qtrace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etemail = findViewById<EditText>(R.id.etemail)
        val etqcid = findViewById<EditText>(R.id.etqcId)
        val btnlogin = findViewById<Button>(R.id.btnLogin)

        btnlogin.setOnClickListener {
            val inputEmail = etemail.text.toString().trim()
            val inputQcId = etqcid.text.toString().trim()

            if (inputEmail.isNotEmpty() && inputQcId.isNotEmpty()) {
                performDatabaseLogin(inputEmail, inputQcId)
            } else {
                Toast.makeText(this, "Please enter Email and QC ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performDatabaseLogin(email: String, qcId: String) {
        // NOTE: If your email is nested in 'details', change "email" to "details.email"
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Email not found in database", Toast.LENGTH_SHORT).show()
                } else {
                    val userDoc = documents.documents[0]
                    val dbQcId = userDoc.getString("qcId")

                    if (dbQcId == qcId) {
                        loginSuccess(userDoc.id)
                    } else {
                        Toast.makeText(this, "Incorrect QC ID", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Database Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginSuccess(userId: String) {
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

        val sharedPref = getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("USER_ID", userId)
            putBoolean("IS_LOGGED_IN", true)
            apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        // Clear back stack so user can't press back to login screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // 🛠️ KEY CHANGE: Tell MainActivity to open the Account tab
        intent.putExtra("TARGET_FRAGMENT", "ACCOUNT")

        startActivity(intent)
        finish()
    }
}