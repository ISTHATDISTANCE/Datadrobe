package com.example.datadrobe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Signup : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun backPress(view: View?) {
        onBackPressedDispatcher.onBackPressed()
    }

    fun register(view: View?) {
        val email = findViewById<EditText>(R.id.emailCreate)
        val password = findViewById<EditText>(R.id.passwordCreate)
        val passwordConfirm = findViewById<EditText>(R.id.passwordConfirm)
        if (email.text.toString() == "") {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.text.toString() == "") {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            return
        }
        if (passwordConfirm.text.toString() == "") {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.text.toString() != passwordConfirm.text.toString()) {
            Toast.makeText(this, "Two passwords should match!", Toast.LENGTH_SHORT).show()
            return
        }
        val db = Firebase.firestore
        val data = hashMapOf(
            "email" to email.text.toString(),
            "password" to password.text.toString()
        )
        val progressBar = findViewById<ProgressBar>(R.id.progressBarSignUp)
        progressBar.visibility = View.VISIBLE
        db.collection("users").document(email.text.toString())
            .set(data)
            .addOnSuccessListener {
                Log.d("lalala", "Registered successfully!")
                progressBar.visibility = View.INVISIBLE
                val intent = Intent(this, Onboarding::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.e("wuwuwu", it.message!!)
            }
    }
}