package com.example.datadrobe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun realLogin(view: View?) {
        val email = findViewById<EditText>(R.id.emailInput)
        val password = findViewById<EditText>(R.id.passwordInput)
        if (email.text.toString() == "") {
            Toast.makeText(this, "Email should not be empty!", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.text.toString() == "") {
            Toast.makeText(this, "Password should not be empty!", Toast.LENGTH_SHORT).show()
            return
        }
        val db = Firebase.firestore
        Log.d("gugugu", email.text.toString())
        val userInfo = db.collection("users").document(email.text.toString())
        userInfo.get()
            .addOnSuccessListener { doc ->
                Log.d("hahaha", doc.data.toString())
                if (doc != null && doc.data != null && doc.data!!["password"] == password.text.toString()) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                } else {
                    Toast.makeText(this, "Invalid email/password!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Invalid email/password!", Toast.LENGTH_SHORT).show()
            }
    }

    fun backPress(view: View?) {
        onBackPressedDispatcher.onBackPressed()
    }
}