package com.example.worknest

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.worknest.database.DatabaseManager
import com.example.worknest.models.Task


class MainActivity : ComponentActivity() {
    private lateinit var dbManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = DatabaseManager(this)

        setContent {
            // Set the WorkNestApp composable as the content view
            WorkNestApp(dbManager)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkNestApp(dbManager: DatabaseManager) {
    var crewCount by remember { mutableStateOf(dbManager.getAllCrew().size) }
    val allTasks by remember { mutableStateOf(dbManager.getAllTasks()) }
    val pendingTasksCount = allTasks.count { !it.completed }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("\uD83C\uDFE0 WorkNest Dashboard", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF00796B),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        HomePage(
            modifier = Modifier.padding(innerPadding),
            crewCount = crewCount,
            pendingTasksCount = pendingTasksCount,
            allTasks = allTasks
        )
    }
}

@Composable
fun BottomNavigationBar() {
    val context = LocalContext.current
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { val intent = Intent(context, CrewManagement::class.java)
                context.startActivity(intent) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Crew Management") },
            label = { Text("Crew") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, TaskManagement::class.java)
                context.startActivity(intent)
            },
            icon = { Icon(Icons.Filled.List, contentDescription = "Task Management") },
            label = { Text("Tasks") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { val intent = Intent(context, ExpenseScreen::class.java)
                context.startActivity(intent) },
            icon = { Icon(Icons.Filled.Money, contentDescription = "Expenses") },
            label = { Text("Expenses") }
        )
    }
}

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    crewCount: Int,
    pendingTasksCount: Int,
    allTasks: List<Task>
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "📊 Quick Stats",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(label = "\uD83D\uDC68\u200D\uD83D\uDD27 Crew Members", value = crewCount)
            StatCard(label = "⌛ Pending Tasks", value = pendingTasksCount)
            StatCard(label = "📅 Deadlines", value = 2)  // You can dynamically update this if you have deadlines.
        }
        Divider(color = Color.Gray, thickness = 2.dp, modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = "\uD83D\uDCDD Recent Tasks",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 16.dp)
        ) {
            items(allTasks) { task ->
                TaskItem(task = task)
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF00796B)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value.toString(), style = MaterialTheme.typography.titleLarge, color = Color.White)
        }
    }
}

@Composable
fun TaskItem(task: Task, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
