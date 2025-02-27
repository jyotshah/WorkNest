/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A01
Date : 2/13/2025
File : MainActivity.kt
*/
package com.example.worknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Money
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.worknest.database.DatabaseManager

// Function Name: onCreate
// Function Description: Starts the activity and sets the content view to the WorkNestApp composable.
class MainActivity : ComponentActivity() {
    private lateinit var dbManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = DatabaseManager(this)
        setContent {
            // Set the WorkNestApp composable as the content view
            WorkNestApp()
        }
    }
}

// Function Name: WorkNestApp
// Function Description: A composable that creates the app's scaffold with top bar, bottom bar, and main content.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkNestApp() {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("\uD83C\uDFE0 WorkNest Dashboard", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF00796B),
                    titleContentColor = Color.White
                )
            )
        },
        // Bottom navigation bar for app navigation
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        // Main content of the app
        HomePage(modifier = Modifier.padding(innerPadding))
    }
}

// Function Name: BottomNavigationBar
// Function Description: Composable to display the bottom navigation bar with three navigation items: Crew, Tasks, and Expenses.
@Composable
fun BottomNavigationBar() {
    val context = LocalContext.current
    NavigationBar {
        // Navigation item for Crew Management
        NavigationBarItem(
            selected = false,
            onClick = { val intent = Intent(context, CrewManagement::class.java)
                context.startActivity(intent) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Crew Management") },
            label = { Text("Crew") }
        )
        // Navigation item for Task Management
        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, TaskManagement::class.java)
                context.startActivity(intent)
            },
            icon = { Icon(Icons.Filled.List, contentDescription = "Task Management") },
            label = { Text("Tasks") }
        )
        // Navigation item for Expense Management
        NavigationBarItem(
            selected = false,
            onClick = { val intent = Intent(context, ExpenseScreen::class.java)
                context.startActivity(intent) },
            icon = { Icon(Icons.Filled.Money, contentDescription = "Expenses") },
            label = { Text("Expenses") }
        )
    }
}

// Function Name: HomePage
// Function Description: The main content of the home page, displaying statistics and recent tasks
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    // Example data
    val crewCount = 10
    val pendingTasks = 5
    val upcomingDeadlines = 3
    val recentTasks = listOf("Kitchen Prep", "Stock Management", "Catering Management")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB))
                )
            )
            .padding(16.dp)
    ) {
        // App Title
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
            StatCard(label = "⌛ Pending Tasks", value = pendingTasks)
            StatCard(label = "📅 Deadlines", value = upcomingDeadlines)
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
            items(recentTasks) { task ->
                TaskItem(task = task)
            }
        }
    }
}

// Function Name: StatCard
// Function Description: Displays a stat card with a label and a value (e.g., Crew Members, Pending Tasks).
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

// Function Name: TaskItem
// Function Description: Displays a single task in a list with a task name and an icon.
@Composable
fun TaskItem(task: String, modifier: Modifier = Modifier) {
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
                text = task,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}