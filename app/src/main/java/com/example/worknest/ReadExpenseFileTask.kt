/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A02
Date : 3/16/2025
File : ReadExpenseFileTask.kt
*/

package com.example.worknest
import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream

class ReadExpenseFileTask (
    private val context: Context,
    private val fileName: String,
    private val onFileRead: (String) -> Unit
) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String {
        return try {
            // Locate the file in Downloads
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            if (!file.exists()) {
                Log.e("ExpenseRead", "File not found: ${file.absolutePath}")
                return "Error: File not found!"
            }

            val fileInputStream = FileInputStream(file)
            val content = fileInputStream.bufferedReader().use { it.readText() }
            fileInputStream.close()

            Log.d("ExpenseRead", "File read successful")
            content
        } catch (e: Exception) {
            Log.e("ExpenseRead", "Error reading file: ${e.message}")
            "Error reading file!"
        }
    }

    override fun onPostExecute(result: String) {
        onFileRead(result)  // Update the UI with file content
    }
}
