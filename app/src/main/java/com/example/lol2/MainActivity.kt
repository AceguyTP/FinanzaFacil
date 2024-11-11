package com.example.lol2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        val welcomeMessage = if (userEmail != null) {
            "Bienvenido, $userEmail"
        } else {
            "Bienvenido, ingrese"
        }

        welcomeTextView.text = welcomeMessage

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.visibility = if (userEmail == null) View.VISIBLE else View.GONE
        logoutButton.visibility = if (userEmail == null) View.GONE else View.VISIBLE
        registerButton.visibility = if (userEmail == null) View.VISIBLE else View.GONE
    }
}