package com.example.lol2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Inicio : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = intent.getStringExtra("USER_ID") ?: ""

        val expenseNameEditText = findViewById<EditText>(R.id.expenseNameEditText)
        val expenseCostEditText = findViewById<EditText>(R.id.expenseAmountEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val totalExpensesTextView = findViewById<TextView>(R.id.totalExpensesTextView)

        saveButton.setOnClickListener {
            val expenseName = expenseNameEditText.text.toString()
            val expenseAmount = expenseCostEditText.text.toString().toDoubleOrNull() ?: 0.0
            val paymentDate = System.currentTimeMillis() // Get current date and time
            val expense = Expense(expenseName, expenseAmount.toString(), paymentDate)

            userId = auth.currentUser?.uid ?: "" // Initialize userId in onCreate()



            database.reference.child("users").child(userId).child("expenses").push()
                .setValue(expense) //
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
                    // Clear input fields after saving
                    expenseNameEditText.text.clear()
                    expenseCostEditText.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
                }
            }
            val expensesRef = database.reference.child("users").child(userId).child("expenses")
            expensesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalExpenses = 0.0
                    for (expenseSnapshot in snapshot.children) {
                        val expense = expenseSnapshot.getValue(Expense::class.java)
                        expense?.let { totalExpenses += it.amount.toDouble() } // Access amount safely
                    }
                    totalExpensesTextView.text = "Total Expenses: $totalExpenses"
                    // ... (Schedule notifications)
                }

                // ... (onCancelled)
                override fun onCancelled(error: DatabaseError) {
                    // Handle database error here (e.g., log the error)
                    Log.w("Inicio", "Failed to read value.", error.toException())
                }

            })
        }
    }
