package com.example.lol2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.work.ExistingPeriodicWorkPolicy
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import java.util.concurrent.TimeUnit

class Inicio : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        userId = intent.getStringExtra("USER_ID") ?: ""

        val expenseNameEditText = findViewById<EditText>(R.id.expenseNameEditText)
        val expenseCostEditText = findViewById<EditText>(R.id.expenseAmountEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val totalExpensesTextView = findViewById<TextView>(R.id.totalExpensesTextView)

        saveButton.setOnClickListener {
            val expenseName = expenseNameEditText.text.toString()
            val expenseAmount = expenseCostEditText.text.toString().toDoubleOrNull() ?: 0.0
            val paymentDate = System.currentTimeMillis() // Get current date and time
            val expense = Expense(expenseName, expenseAmount, paymentDate)

            userId = auth.currentUser?.uid ?: "" // Initialize userId in onCreate()



            val expensesSave = db.collection("users").document(userId).collection("expenses")
            expensesSave.add(expense)
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
                    // ... clear input fields
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
                }

            val expensestotal = db.collection("users").document(userId).collection("expenses")
            expensestotal.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("Inicio", "Listen failed.", error)
                    return@addSnapshotListener
                }

                var totalExpenses = 0.0
                for (document in snapshot!!) {
                    val expense = document.toObject(Expense::class.java)
                    totalExpenses += expense.amount
                }
                totalExpensesTextView.text = "Gasto Total: $totalExpenses"
            }

        }

        scheduleExpenseReminderWorker()
    }

    private fun scheduleExpenseReminderWorker() {
        val constraints = Constraints.Builder()
            // ... (Set constraints)
            .build()

        val expenseReminderRequest = PeriodicWorkRequestBuilder<ExpenseReminderWorker>(2, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "expenseReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            expenseReminderRequest
        )
    }

}
