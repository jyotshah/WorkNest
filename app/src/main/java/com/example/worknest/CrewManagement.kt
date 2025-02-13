package com.example.worknest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class CrewMember(val id: Int, val name: String, var role: String, val availability: String)

class CrewManagement : ComponentActivity() {
    private val crewList = mutableStateListOf<CrewMember>( // Initial list of crew members
        CrewMember(1, "Alice", "Pilot", "Available"),
        CrewMember(2, "Bob", "Engineer", "Unavailable")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrewListScreen(
                crewList = crewList,
                onDeleteCrewClick = { crewMember ->
                    crewList.remove(crewMember)
                    Toast.makeText(this, "${crewMember.name} deleted", Toast.LENGTH_SHORT).show()
                },
                onAddCrewClick = { name, role, availability ->
                    val newId = crewList.size + 1
                    crewList.add(CrewMember(newId, name, role, availability))
                    Toast.makeText(this, "$name added", Toast.LENGTH_SHORT).show()
                },
                onEditCrewClick = { id, newName, newRole, newAvailability ->
                    val index = crewList.indexOfFirst { it.id == id }
                    if (index != -1) {
                        crewList[index] = CrewMember(id, newName, newRole, newAvailability)
                        Toast.makeText(this, "$newName updated", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrewListScreen(
    crewList: List<CrewMember>,
    onDeleteCrewClick: (CrewMember) -> Unit,
    onAddCrewClick: (String, String, String) -> Unit,
    onEditCrewClick: (Int, String, String, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var crewMemberToEdit by remember { mutableStateOf<CrewMember?>(null) }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crew Management") },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Crew Member")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(crewList) { crewMember ->
                    CrewMemberCard(
                        crewMember = crewMember,
                        onDeleteCrewClick = onDeleteCrewClick,
                        onEditCrewClick = { crewMemberToEdit = it }
                    )
                }
            }

            // Dialog for adding a new crew member
            if (showDialog) {
                AddCrewDialog(
                    onDismiss = { showDialog = false },
                    onAddCrew = { name, role, availability ->
                        onAddCrewClick(name, role, availability)
                        showDialog = false
                    },
                    name = name,
                    onNameChange = { name = it },
                    role = role,
                    onRoleChange = { role = it },
                    availability = availability,
                    onAvailabilityChange = { availability = it }
                )
            }

            // Dialog for editing an existing crew member
            crewMemberToEdit?.let { crew ->
                EditCrewDialog(
                    crewMember = crew,
                    onDismiss = { crewMemberToEdit = null },
                    onEditCrew = { updatedName, updatedRole, updatedAvailability ->
                        onEditCrewClick(crew.id, updatedName, updatedRole, updatedAvailability)
                        crewMemberToEdit = null
                    }
                )
            }
        }
    }
}

@Composable
fun CrewMemberCard(
    crewMember: CrewMember,
    onDeleteCrewClick: (CrewMember) -> Unit,
    onEditCrewClick: (CrewMember) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${crewMember.name}", style = MaterialTheme.typography.titleMedium)
            Text("Role: ${crewMember.role}", style = MaterialTheme.typography.bodyLarge)
            Text("Availability: ${crewMember.availability}", style = MaterialTheme.typography.bodySmall)

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                TextButton(onClick = { onEditCrewClick(crewMember) }) {
                    Text("Edit")
                }
                TextButton(onClick = { onDeleteCrewClick(crewMember) }) {
                    Text("Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCrewDialog(
    onDismiss: () -> Unit,
    onAddCrew: (String, String, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    role: String,
    onRoleChange: (String) -> Unit,
    availability: String,
    onAvailabilityChange: (String) -> Unit
) {
    val availableRoles = listOf("Waiter", "Chef", "Manager", "Cashier", "Dishwasher")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Crew Member") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Role:")
                availableRoles.forEach { availableRole ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = role == availableRole,
                            onClick = { onRoleChange(availableRole) }
                        )
                        Text(availableRole, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = availability,
                    onValueChange = onAvailabilityChange,
                    label = { Text("Availability") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && role.isNotBlank() && availability.isNotBlank()) {
                        onAddCrew(name, role, availability)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCrewDialog(
    crewMember: CrewMember,
    onDismiss: () -> Unit,
    onEditCrew: (String, String, String) -> Unit
) {
    val availableRoles = listOf("Waiter", "Chef", "Manager", "Cashier", "Dishwasher")
    var name by remember { mutableStateOf(crewMember.name) }
    var role by remember { mutableStateOf(crewMember.role) }
    var availability by remember { mutableStateOf(crewMember.availability) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Crew Member") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Role:")
                availableRoles.forEach { availableRole ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = role == availableRole,
                            onClick = { role = availableRole }
                        )
                        Text(availableRole, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = availability,
                    onValueChange = { availability = it },
                    label = { Text("Availability") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && role.isNotBlank() && availability.isNotBlank()) {
                        onEditCrew(name, role, availability)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
