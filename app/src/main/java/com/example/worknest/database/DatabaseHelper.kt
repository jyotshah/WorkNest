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

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_CREW)
        db.execSQL(CREATE_TABLE_TASKS)
        db.execSQL(CREATE_TABLE_EXPENSES)
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

        private const val CREATE_TABLE_CREW =
            "CREATE TABLE crew (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, role TEXT, availability TEXT)"
        private const val CREATE_TABLE_TASKS =
            "CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, priority TEXT, completed INTEGER)"
        private const val CREATE_TABLE_EXPENSES =
            "CREATE TABLE expenses (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, category TEXT, amount REAL, date TEXT)"

    }
}
