package com.example.datadrobe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout

class Onboarding : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
    }

    fun continueBtnClicked(view: View?) {
        val progressBar = findViewById<ProgressBar>(R.id.onboardingProgressBar)
        if (progressBar.progress < progressBar.max) {
            val page1 = findViewById<ConstraintLayout>(R.id.page1)
            val page2 = findViewById<ConstraintLayout>(R.id.page2)
            val progressText = findViewById<TextView>(R.id.onboardingProgressText)
            progressBar.progress = progressBar.max
            progressText.text = "2 of 2"
            page1.visibility = View.GONE
            page2.visibility = View.VISIBLE
        } else {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}