/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A02
Date : 3/2/2025
File : DatabaseManager.kt
*/
package com.example.worknest.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.worknest.models.CrewMember
import com.example.worknest.models.Task
import com.example.worknest.models.Expense

class DatabaseManager(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    // Crew Operations
    fun insertCrew(name: String, role: String, availability: String, linkedInUrl: String) {
        val values = ContentValues().apply {
            put("name", name)
            put("role", role)
            put("availability", availability)
            put("linkedInUrl", linkedInUrl)
        }
        db.insert("crew", null, values)
    }

    @SuppressLint("Range")
    fun getAllCrew(): List<CrewMember> {
        val crewList = mutableListOf<CrewMember>()
        val cursor: Cursor = db.query(
            "crew", arrayOf("id", "name", "role", "availability", "linkedInUrl"),
            null, null, null, null, null
        )

        val nameIndex = cursor.getColumnIndex("name")
        val roleIndex = cursor.getColumnIndex("role")
        val availabilityIndex = cursor.getColumnIndex("availability")
        val linkedInUrlIndex = cursor.getColumnIndex("linkedInUrl")

        while (cursor.moveToNext()) {
            if (nameIndex >= 0 && roleIndex >= 0 && availabilityIndex >= 0 && linkedInUrlIndex >= 0) {
                val crewMember = CrewMember(
                    id = cursor.getInt(cursor.getColumnIndex("id")),
                    name = cursor.getString(nameIndex),
                    role = cursor.getString(roleIndex),
                    availability = cursor.getString(availabilityIndex),
                    linkedInUrl = cursor.getString(linkedInUrlIndex)
                )
                crewList.add(crewMember)
            }
        }
        cursor.close()
        return crewList
    }

    fun updateCrew(id: Int, name: String, role: String, availability: String, linkedInUrl: String) {
        val values = ContentValues().apply {
            put("name", name)
            put("role", role)
            put("availability", availability)
            put("linkedInUrl", linkedInUrl)
        }
        db.update("crew", values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteCrew(id: Int) {
        db.delete("crew", "id = ?", arrayOf(id.toString()))
    }

    // Task Operations
    fun insertTask(name: String, description: String, priority: String, completed: Boolean) {
        val values = ContentValues().apply {
            put("name", name)
            put("description", description)
            put("priority", priority)
            put("completed", if (completed) 1 else 0)
        }
        db.insert("tasks", null, values)
    }

    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM tasks", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val description = cursor.getString(2)
            val priority = cursor.getString(3)
            val completed = cursor.getInt(4) == 1
            taskList.add(Task(id, name, description, priority, completed))
        }
        cursor.close()
        return taskList
    }

    fun deleteTask(id: Int) {
        db.delete("tasks", "id=?", arrayOf(id.toString()))
    }

    fun updateTask(task: Task) {
        val values = ContentValues().apply {
            put("name", task.name)
            put("description", task.description)
            put("priority", task.priority)
            put("completed", if (task.completed) 1 else 0)
        }
        db.update("tasks", values, "id=?", arrayOf(task.id.toString()))
    }

    // Expense Operations
    fun insertExpense(name: String, category: String, amount: Double, date: String) {
        val roundedAmount = String.format("%.2f", amount).toDouble()

        val values = ContentValues().apply {
            put("name", name)
            put("category", category)
            put("amount", roundedAmount)
            put("date", date)
        }
        db.insert("expenses", null, values)
    }

    fun getAllExpenses(): List<Expense> {
        val expenseList = mutableListOf<Expense>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM expenses", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val category = cursor.getString(2)
            val amount = String.format("%.2f", cursor.getDouble(3)).toDouble()
            val date = cursor.getString(4)
            expenseList.add(Expense(id, name, category, amount, date))
        }
        cursor.close()
        return expenseList
    }
}
