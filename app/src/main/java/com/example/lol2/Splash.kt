package com.example.lol2

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = Firebase.auth

        // Verifica si hay un usuario autenticado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Usuario autenticado, redirige a la pantalla principal
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Usuario no autenticado, redirige a la pantalla de inicio de sesión
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish() // Finaliza la Splash Activity para que no se pueda volver atrás
    }
}