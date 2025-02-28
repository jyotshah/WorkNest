/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A01
Date : 2/13/2025
File : TaskManagement.kt
*/
package com.example.worknest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.worknest.database.DatabaseManager
import com.example.worknest.models.Task

class TaskManagement : ComponentActivity() {
    private lateinit var dbManager: DatabaseManager
    private val taskList = mutableStateListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = DatabaseManager(this)
        taskList.addAll(dbManager.getAllTasks())

        setContent {
            TaskManagementScreen(
                tasks = taskList,
                onDeleteTaskClick = { task ->
                    dbManager.deleteTask(task.id)
                    taskList.remove(task)
                    Toast.makeText(this, "${task.name} deleted", Toast.LENGTH_SHORT).show()
                },
                onAddTaskClick = { name, description, priority ->
                    val newTask = Task(0, name, description, priority, false)
                    dbManager.insertTask(name, description, priority, false)
                    taskList.add(newTask)
                    Toast.makeText(this, "$name added", Toast.LENGTH_SHORT).show()
                },
                onEditTaskClick = { id, newName, newDescription, newPriority ->
                    val updatedTask = Task(id, newName, newDescription, newPriority, false)
                    dbManager.updateTask(updatedTask)
                    val index = taskList.indexOfFirst { it.id == id }
                    if (index != -1) {
                        taskList[index] = updatedTask.copy()
                        Toast.makeText(this, "$newName updated", Toast.LENGTH_SHORT).show()
                    }
                },
                onTaskCompleted = { task, completed ->
                    val updatedTask = task.copy(completed = completed)
                    dbManager.updateTask(updatedTask)
                    val index = taskList.indexOfFirst { it.id == task.id }
                    if (index != -1) {
                        taskList[index] = updatedTask.copy()
                    }
                }
            )
        }
    }
}

// Function: TaskManagementScreen
// Description: Displays a list of tasks with options to edit, delete, and mark as completed.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagementScreen(
    tasks: List<Task>,
    onDeleteTaskClick: (Task) -> Unit,
    onAddTaskClick: (String, String, String) -> Unit,
    onEditTaskClick: (Int, String, String, String) -> Unit,
    onTaskCompleted: (Task, Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📝 Task Management", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF00796B),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB))
                    )
                )
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Button(onClick = { showDialog = true }, modifier = Modifier.padding(vertical = 8.dp)) {
                Text(text = "Add Task")
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 16.dp)) {
                items(tasks) { task ->
                    TaskCard(
                        task,
                        onCheckedChange = { completed -> onTaskCompleted(task, completed) },
                        onEdit = { taskToEdit = task; showDialog = true },
                        onDelete = { onDeleteTaskClick(task) }
                    )
                }
            }

            if (showDialog) {
                AddOrEditTaskDialog(
                    task = taskToEdit,
                    onDismiss = { showDialog = false; taskToEdit = null },
                    onConfirm = { name, description, priority ->
                        if (taskToEdit == null) {
                            onAddTaskClick(name, description, priority)
                        } else {
                            onEditTaskClick(taskToEdit!!.id, name, description, priority)
                        }
                        showDialog = false
                        taskToEdit = null
                    }
                )
            }
        }
    }
}

// Function: TaskCard
// Description: Displays a task with options to edit, delete, and mark as completed.
@Composable
fun TaskCard(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Checkbox(
            checked = task.completed,
            onCheckedChange = { completed -> onCheckedChange(completed) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Task: ${task.name}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Description: ${task.description}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Priority: ${task.priority}", color = Color.Blue, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onEdit) { Text("Edit") }
        Spacer(modifier = Modifier.width(4.dp))
        Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(Color.Red)) { Text("Delete") }
    }
    Divider(modifier = Modifier.padding(vertical = 8.dp))
}


// Function: AddOrEditTaskDialog
// Description: Dialog box to add or edit a task.
@Composable
fun AddOrEditTaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(task?.name ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: "Medium") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Add Task" else "Edit Task") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Task Name") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                PriorityDropdown(priority) { priority = it }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, description, priority) }) { Text("Save") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityDropdown(selectedPriority: String, onPrioritySelected: (String) -> Unit) {
    val priorities = listOf("Low", "Medium", "High")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedPriority,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Priority") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            priorities.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority) },
                    onClick = {
                        onPrioritySelected(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}
