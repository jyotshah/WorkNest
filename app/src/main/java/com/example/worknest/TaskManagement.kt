package com.example.worknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class TaskManagement : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskManagementScreen()
        }
    }
}

@Composable
fun TaskManagementScreen() {
    // States for the task's title, description and the task list
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var tasks by remember { mutableStateOf(listOf<Task>()) }

    // Function to save a new task
    val saveTask: () -> Unit = {
        // Create a new task object and add it to the task list
        val newTask = Task(taskName, taskDescription, false) // Initially set to not completed
        tasks = tasks + newTask
        taskName = ""  // Clear the input fields after saving
        taskDescription = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Screen Title
        Text(
            text = "Task Management",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Task Creation/Editing Section

        // 1. Task Name Input Field
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Enter task name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // 2. Task Description Input Field
        OutlinedTextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            label = { Text("Enter task description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Save Button
        Button(
            onClick = saveTask,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Save Task")
        }

        Spacer(modifier = Modifier.height(16.dp))


        Spacer(modifier = Modifier.height(8.dp))

        // Display saved tasks with checkboxes
        tasks.forEachIndexed { index, task ->
            TaskCard(task, onCheckedChange = { checked ->
                tasks = tasks.toMutableList().apply {
                    this[index] = task.copy(completed = checked) // Update the task's completion status
                }
            })
        }
    }
}

// Task data class
data class Task(val name: String, val description: String, val completed: Boolean)

@Composable
fun TaskCard(task: Task, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Checkbox for task completion
        Checkbox(
            checked = task.completed,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "Task: ${task.name}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Description: ${task.description}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    Divider(modifier = Modifier.padding(vertical = 8.dp))
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskManagementScreen()
}
