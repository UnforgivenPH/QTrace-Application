package com.example.qtrace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ImageView

class LoginActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 🛠️ Bind to the new TextInputEditText views
        val etEmail = findViewById<TextInputEditText>(R.id.etemail)
        val etQcId = findViewById<TextInputEditText>(R.id.etqcId)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnClose = findViewById<ImageView>(R.id.btnCloseLogin)

        btnClose.setOnClickListener {
            finish()
        }

        btnLogin.setOnClickListener {
            val inputEmail = etEmail.text.toString().trim()
            val inputQcId = etQcId.text.toString().trim()

            if (inputEmail.isNotEmpty() && inputQcId.isNotEmpty()) {
                performDatabaseLogin(inputEmail, inputQcId)
            } else {
                Toast.makeText(this, "Please enter Email and QC ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performDatabaseLogin(email: String, qcId: String) {
        // Query 'users' collection where 'details.email' (or just 'email') matches
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Login Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginSuccess(userId: String) {
        Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()

        val sharedPref = getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("USER_ID", userId)
            putBoolean("IS_LOGGED_IN", true)
            apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Signal MainActivity to open the Account tab immediately
        intent.putExtra("TARGET_FRAGMENT", "ACCOUNT")

        startActivity(intent)
        finish()
    }
}