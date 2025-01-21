package com.example.worknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class TaskManagementScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskFormScreen()
        }
    }
}

@Composable
fun TaskFormScreen() {
    var taskName by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf("Low") }
    var taskStatus by remember { mutableStateOf("Not Started") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Assign New Task", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = taskPriority,
            onValueChange = { taskPriority = it },
            label = { Text("Priority (Low, Medium, High)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = taskStatus,
            onValueChange = { taskStatus = it },
            label = { Text("Status") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Save task action
        }) {
            Text("Save Task")
        }
    }
}