package com.example.lol2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.setText
import androidx.compose.ui.semantics.text
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Inicio : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var remainingBudgetTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var currentBudgetEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        remainingBudgetTextView = findViewById(R.id.remainingBudgetTextView)
        sharedPreferences = getSharedPreferences("budgetPrefs", MODE_PRIVATE)
        currentBudgetEditText = findViewById(R.id.currentBudgetEditText)

        val expenseNameEditText = findViewById<EditText>(R.id.expenseNameEditText)
        val expenseCostEditText = findViewById<EditText>(R.id.expenseCostEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val expenseName = expenseNameEditText.text.toString()
            val expenseCost = expenseCostEditText.text.toString().toDoubleOrNull() ?: 0.0
            val currentBudget = sharedPreferences.getFloat("currentBudget", 0f).toDouble()

            val remainingBudget = currentBudget.minus(expenseCost)
            remainingBudgetTextView.text = "Remaining Budget: $remainingBudget"

            val userId = auth.currentUser?.uid ?: return@setOnClickListener // Get user ID

            val expenseData = hashMapOf(
                "expenseName" to expenseName,
                "expenseCost" to expenseCost,
                "currentBudget" to currentBudget
            )

            database.reference.child("users").child(userId).child("expenses").push()
                .setValue(expenseData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
                    // Clear input fields after saving
                    expenseNameEditText.text.clear()
                    expenseCostEditText.text.clear()

                    val remainingBudget = currentBudget - expenseCost
                    remainingBudgetTextView.text = "Remaining Budget: $remainingBudget"
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
                }
        }
    }
}