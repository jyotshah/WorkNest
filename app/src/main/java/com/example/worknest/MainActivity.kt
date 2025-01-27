package com.example.worknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkNestApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkNestApp() {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { TopAppBar(title = { Text("WorkNest") }) },
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        HomePage(modifier = Modifier.padding(innerPadding))
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
            onClick = { val intent = Intent(context, TaskManagementScreen::class.java)
                context.startActivity(intent) },
            icon = { Icon(Icons.Filled.List, contentDescription = "Task Management") },
            label = { Text("Tasks") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { val intent = Intent(context, ReportsScreen::class.java)
                context.startActivity(intent) },
            icon = { Icon(Icons.Filled.Description, contentDescription = "Reports") },
            label = { Text("Reports") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { val intent = Intent(context, ChatScreen::class.java)
                context.startActivity(intent) },
            icon = { Icon(Icons.Filled.Message, contentDescription = "Chat Room") },
            label = { Text("Chats") }
        )
    }
}

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    // Example data
    val crewCount = 10
    val pendingTasks = 5
    val upcomingDeadlines = 3
    val recentTasks = listOf("Task 1", "Task 2", "Task 3")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Title
        Text(
            text = "WorkNest Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Quick Stats Section
        Text(
            text = "Quick Stats",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(label = "Crew Members", value = crewCount)
            StatCard(label = "Pending Tasks", value = pendingTasks)
            StatCard(label = "Upcoming Deadlines", value = upcomingDeadlines)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Recent Tasks Section
        Text(
            text = "Recent Tasks",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            items(recentTasks) { task ->
                TaskItem(task = task)
            }
        }

        // Add Task/Project Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FloatingActionButton(
                onClick = { TaskManagementScreen() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task or Project")
            }
        }
    }
}


@Composable
fun StatCard(label: String, value: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(4.dp),
        elevation = CardDefaults.outlinedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value.toString(), style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun TaskItem(task: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.List,
                contentDescription = "Task Icon",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}