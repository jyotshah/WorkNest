package com.example.worknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


class CrewManagement : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddEditCrewFormScreen()
        }
    }
}

@Composable
fun AddEditCrewFormScreen() {
    var crewName by remember { mutableStateOf("") }
    var crewRole by remember { mutableStateOf("") }
    var crewAvailability by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add New Crew Member", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = crewName,
            onValueChange = { crewName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = crewRole,
            onValueChange = { crewRole = it },
            label = { Text("Role") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = crewAvailability,
            onValueChange = { crewAvailability = it },
            label = { Text("Availability") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Add crew member action
        }) {
            Text("Save Crew Member")
        }
    }
}