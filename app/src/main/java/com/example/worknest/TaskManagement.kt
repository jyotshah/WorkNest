package com.example.worknest

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.input.TextFieldValue

data class Task(
    val id: Int,
    var taskName: String,
    var taskPriority: String,
    var taskStatus: String,
    var assignee: String,
    var completedBy: String?,
    var progress: Int
)

class TaskManagementScreen : ComponentActivity() {
    private val taskList = mutableStateListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskManagementScreenUI(taskList = taskList)
        }
    }
}

@Composable
fun TaskManagementScreenUI(taskList: MutableList<Task>) {
    var showAddTaskForm by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showToast by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showToast) {
        Toast.makeText(context, "Task added!", Toast.LENGTH_SHORT).show()
        showToast = false
    }

    if (showAddTaskForm) {
        TaskFormScreen(
            taskToEdit = selectedTask,
            onSaveTask = { task ->
                if (selectedTask == null) {
                    taskList.add(task) // Add the task to the list
                    showToast = true // Trigger the toast to show the task was added
                } else {
                    taskList[taskList.indexOf(selectedTask)] = task // Update the existing task
                    showToast = true // Trigger the toast for update
                }
                showAddTaskForm = false // Close the form after saving
                selectedTask = null
            },
            onCancel = {
                showAddTaskForm = false // Close the form if canceled
            },
            onDeleteTask = { task ->
                taskList.remove(task) // Remove the task from the list
                showToast = true // Trigger the toast for task deletion
                showAddTaskForm = false // Close the form after deletion
                selectedTask = null
            }
        )
    } else {
        // Main screen with task list and "Add New Task" button
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Task Management", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(taskList) { task ->
                    TaskItem(
                        task = task,
                        taskNumber = taskList.indexOf(task) + 1,
                        onClick = {
                            selectedTask = task
                            showAddTaskForm = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add New Task button
            Button(onClick = {
                showAddTaskForm = true // Show the task form when clicked
            }) {
                Text("Add New Task")
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, taskNumber: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Task $taskNumber: ${task.taskName}", style = MaterialTheme.typography.titleMedium)
            Text("Priority: ${task.taskPriority}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun TaskFormScreen(
    taskToEdit: Task? = null,
    onSaveTask: (Task) -> Unit,
    onCancel: () -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    var taskName by remember { mutableStateOf(taskToEdit?.taskName ?: "") }
    var taskPriority by remember { mutableStateOf(taskToEdit?.taskPriority ?: "") }
    var taskStatus by remember { mutableStateOf(taskToEdit?.taskStatus ?: "") }
    var assignee by remember { mutableStateOf(taskToEdit?.assignee ?: "") }
    var progress by remember { mutableStateOf(taskToEdit?.progress ?: 0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("${if (taskToEdit == null) "Add" else "Edit"} Task", style = MaterialTheme.typography.titleMedium)

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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = assignee,
            onValueChange = { assignee = it },
            label = { Text("Assignee") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = progress.toString(),
            onValueChange = { progress = it.toIntOrNull() ?: 0 },
            label = { Text("Progress (%)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                val task = Task(
                    id = taskToEdit?.id ?: (System.currentTimeMillis() % 100000).toInt(),
                    taskName = taskName,
                    taskPriority = taskPriority,
                    taskStatus = taskStatus,
                    assignee = assignee,
                    completedBy = null,
                    progress = progress
                )
                onSaveTask(task)
            }) {
                Text("Save Task")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }

        if (taskToEdit != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { taskToEdit?.let { onDeleteTask(it) } },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete Task", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}