/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A02
Date : 3/2/2025
File : DatabaseHelper.kt
*/
package com.example.worknest.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create Crew table
        db.execSQL(
            "CREATE TABLE crew (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "availability TEXT NOT NULL)"
        )

        // Create Tasks table
        db.execSQL(
            "CREATE TABLE tasks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "priority TEXT NOT NULL, " +
                    "completed INTEGER NOT NULL)"
        )

        // Create Expenses table
        db.execSQL(
            "CREATE TABLE expenses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "category TEXT NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "date TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS crew")
        db.execSQL("DROP TABLE IF EXISTS tasks")
        db.execSQL("DROP TABLE IF EXISTS expenses")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "worknest.db"
        private const val DATABASE_VERSION = 1
    }
}
