//TaskReminderWorker.kt
package com.example.worknest

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.worknest.database.DatabaseManager
import com.example.worknest.models.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskReminderWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("TaskReminderWorker", "Worker is running...")
        val dbManager = DatabaseManager(applicationContext)
        val tasks = dbManager.getAllTasks()

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        for (task in tasks) {
            if (!task.completed && isDeadlineNear(task.deadline, currentDate)) {
                NotificationHelper.showNotification(applicationContext, task.name, "Deadline is approaching!")
            }
        }

        return Result.success()
    }

    private fun isDeadlineNear(taskDeadline: String, currentDate: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val deadlineDate = sdf.parse(taskDeadline)
            val todayDate = sdf.parse(currentDate)

            if (deadlineDate != null && todayDate != null) {
                val diff = (deadlineDate.time - todayDate.time) / (1000 * 60 * 60 * 24)
                return diff in 0..1  // Notify if the deadline is today or tomorrow
            }
            false
        } catch (e: Exception) {
            Log.e("TaskReminderWorker", "Error parsing date", e)
            false
        }
    }
}
