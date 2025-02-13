package com.example.worknest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

class TaskManagement : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskManagementScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagementScreen() {
    val context = LocalContext.current
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var tasks = remember { mutableStateListOf<Task>() }

    val saveTask: () -> Unit = {
        if (taskName.isNotBlank() && taskDescription.isNotBlank()) {
            tasks.add(Task(taskName, taskDescription, selectedPriority, false))
            Toast.makeText(context, "Task saved successfully", Toast.LENGTH_SHORT).show()
            taskName = ""
            taskDescription = ""
            selectedPriority = "Medium"
        } else {
            Toast.makeText(context, "Please enter task details", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Task Management",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Enter task name") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            label = { Text("Enter task description") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        PriorityDropdown(selectedPriority) { priority ->
            selectedPriority = priority
        }

        Button(
            onClick = saveTask,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Save Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        tasks.forEachIndexed { index, task ->
            TaskCard(
                task,
                onCheckedChange = { checked ->
                    tasks[index] = task.copy(completed = checked)
                },
                onEdit = {
                    taskName = task.name
                    taskDescription = task.description
                    selectedPriority = task.priority
                    tasks.removeAt(index)
                    Toast.makeText(context, "Task moved to edit", Toast.LENGTH_SHORT).show()
                },
                onDelete = {
                    tasks.removeAt(index)
                    Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

data class Task(val name: String, val description: String, val priority: String, val completed: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityDropdown(selectedPriority: String, onPrioritySelected: (String) -> Unit) {
    val priorities = listOf("Low", "Medium", "High")
    var expanded by remember { mutableStateOf(false) }

    // This will represent the selected item in the spinner
    val label = "Priority: $selectedPriority"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,  // Makes it read-only to mimic a spinner
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
            onCheckedChange = onCheckedChange
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskManagementScreen()
}
