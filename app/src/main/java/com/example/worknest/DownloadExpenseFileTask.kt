/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A02
Date : 3/16/2025
File : DownloadExpenseFileTask.kt
*/
package com.example.worknest

import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.charset.StandardCharsets

class DownloadExpenseFileTask(
    private val context: Context,
    private val fileUrl: String,
    private val filename: String,
    private val onDownloadComplete: () -> Unit
) : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void?): Boolean {
        return try {
            val url = URL(fileUrl)
            val inputStream: InputStream = url.openStream()  // ✅ No HttpURLConnection, just openStream()

            //Save file to Downloads folder
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, filename)
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            val sb = StringBuffer()

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                sb.append(String(buffer, 0, bytesRead, StandardCharsets.UTF_8))
                outputStream.write(buffer, 0, bytesRead)
            }

            Log.d("ExpenseDownload", "Download successful: ${file.absolutePath}")
            outputStream.close()
            inputStream.close()
            true
        } catch (e: Exception) {
            Log.e("ExpenseDownload", "Error: ${e.message}")
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        if (result) {
            Toast.makeText(context, "File saved to Downloads 📂", Toast.LENGTH_LONG).show()
            onDownloadComplete()
        } else {
            Toast.makeText(context, "Download failed!", Toast.LENGTH_SHORT).show()
        }
    }
}
