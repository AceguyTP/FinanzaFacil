package com.example.lol2

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                // Display Snackbar or Toast here
                val rootView = findViewById<View>(android.R.id.content)
                Snackbar.make(
                    rootView,
                    "Por favor, ingresa tu correo electrónico y contraseña",
                    Snackbar.LENGTH_SHORT
                ).show()
                // Or you can use a Toast:
                // Toast.makeText(this, "Por favor, ingresa tu correo electrónico y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Prevent login attempt
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        // ... dentro del listener del botón de inicio de sesión ...

                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success")
                                    val user = auth.currentUser
                                    // ...

                                    // Redirigir a la pantalla principal
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish() // Opcional: Finalizar la actividad de login para que no se pueda volver atrás
                                } else {
                                    // ...
                                }
                            }

                            // ...
                    } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                this,
                                "Correo y/o contraseña incorrectos",
                                Toast.LENGTH_SHORT
                            ).show()
                            // O puedes usar un Snackbar si prefiere
                            //val rootView = findViewById<View>(android.R.id.content)
                            //Snackbar.make(rootView, "Por favor, ingresa tu correo electrónico y contraseña", Snackbar.LENGTH_SHORT).show()
                            return@addOnCompleteListener // Evita que se ejecute el código de inicio de sesión
                    }
                }
        }
    }
}