/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A02
Date : 3/2/2025
File : DatabaseManager.kt
*/
package com.example.worknest.database

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
    fun insertCrew(name: String, role: String, availability: String) {
        val values = ContentValues().apply {
            put("name", name)
            put("role", role)
            put("availability", availability)
        }
        db.insert("crew", null, values)
    }

    fun updateCrew(id: Int, name: String, role: String, availability: String) {
        val values = ContentValues().apply {
            put("name", name)
            put("role", role)
            put("availability", availability)
        }
        db.update("crew", values, "id = ?", arrayOf(id.toString()))
    }

    fun getAllCrew(): List<CrewMember> {
        val crewList = mutableListOf<CrewMember>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM crew", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val role = cursor.getString(2)
            val availability = cursor.getString(3)
            crewList.add(CrewMember(id, name, role, availability))
        }
        cursor.close()
        return crewList
    }

    fun deleteCrew(id: Int) {
        db.delete("crew", "id=?", arrayOf(id.toString()))
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
