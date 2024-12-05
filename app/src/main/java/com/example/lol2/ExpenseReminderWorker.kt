package com.example.lol2

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import android.app.NotificationChannel

class ExpenseReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        try {
            val userId = inputData.getString("userId") ?: ""// Get the user ID (e.g., from shared preferences)
            val expensesCollection = db.collection("users").document(userId).collection("expenses")

            expensesCollection.whereGreaterThanOrEqualTo("paymentDate", getTomorrowTimestamp())
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val expense = document.toObject(Expense::class.java)
                        // Show notification for this expense
                        showNotification(expense)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting expenses", exception)
                    Result.failure()
                }

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in ExpenseReminderWorker", e)
            return Result.retry()
        }
    }

    private fun getTomorrowTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun showNotification(expense: Expense) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "expense_reminder_channel"
        val channelName = "Expense Reminders"

        // Create notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(applicationContext, MainActivity::class.java) // Replace with your target activity
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.logo) // Replace with your notification icon
            .setContentTitle("Recordatorio de Gasto")
            .setContentText("Tienes un gasto proximo: ${expense.name}")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(expense.hashCode(), notificationBuilder.build()) // Use expense.hashCode() as notification ID
    }
    companion object {
        private const val TAG = "ExpenseReminderWorker"
    }
}