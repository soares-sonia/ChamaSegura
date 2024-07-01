package com.example.queimasegura.manager

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.queimasegura.R


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_m)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {

            val intent = Intent(this, ManagerActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
