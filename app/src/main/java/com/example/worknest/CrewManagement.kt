/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A01
Date : 2/13/2025
File : CrewManagement.kt
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.worknest.database.DatabaseManager
import com.example.worknest.models.CrewMember

// Data class representing  crew member
data class CrewMember(val id: Int, val name: String, var role: String, val availability: String)

class CrewManagement : ComponentActivity() {
    private lateinit var dbManager: DatabaseManager
    private val crewList = mutableStateListOf<CrewMember>()


    // Function: onCreate
    // Description: Initializes the application and the UI content
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = DatabaseManager(this)

        crewList.addAll(dbManager.getAllCrew())

        setContent {
            CrewListScreen(
                crewList = crewList,

                onAddCrewClick = { name, role, availability, onCrewCountChange ->
                    val newCrew = CrewMember(0, name, role, availability)
                    dbManager.insertCrew(name, role, availability)
                    crewList.add(newCrew)
                    onCrewCountChange()  // Update the crew count after adding
                    Toast.makeText(this, "$name added", Toast.LENGTH_SHORT).show()
                },

                onDeleteCrewClick = { crewMember, onCrewCountChange ->
                    dbManager.deleteCrew(crewMember.id)
                    crewList.remove(crewMember)
                    onCrewCountChange()  // Update the crew count after deleting
                    Toast.makeText(this, "${crewMember.name} deleted", Toast.LENGTH_SHORT).show()
                },

                onEditCrewClick = { id, newName, newRole, newAvailability ->
                    // Update the database
                    dbManager.updateCrew(id, newName, newRole, newAvailability)

                    // Find the crew member in the local list and update it
                    val updatedCrew = CrewMember(id, newName, newRole, newAvailability)
                    val index = crewList.indexOfFirst { it.id == id }
                    if (index != -1) {
                        crewList[index] = updatedCrew
                        Toast.makeText(this, "$newName updated", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        crewList.clear()
        crewList.addAll(dbManager.getAllCrew())
    }
}

// Function: CrewListScreen
// Description: Displays the list of crew members and provides options to add, edit, or delete them
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrewListScreen(
    crewList: MutableList<CrewMember>,
    onDeleteCrewClick: (CrewMember, () -> Unit) -> Unit,
    onAddCrewClick: (String, String, String, () -> Unit) -> Unit,
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
                title = { Text("👥 Crew Management", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF00796B),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Crew Member")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB))
                    )
                )
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(crewList) { crewMember ->
                    CrewMemberCard(
                        crewMember = crewMember,
                        onDeleteCrewClick = { onDeleteCrewClick(crewMember, {}) },
                        onEditCrewClick = { crewMemberToEdit = it }
                    )
                }
            }

            if (showDialog) {
                AddCrewDialog(
                    onDismiss = { showDialog = false },
                    onAddCrew = { name, role, availability ->
                        onAddCrewClick(name, role, availability, {})
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

// Function: CrewMemberCard
// Description: Displays information about a crew member with edit and delete options
@Composable
fun CrewMemberCard(
    crewMember: CrewMember,
    onDeleteCrewClick: (CrewMember) -> Unit,
    onEditCrewClick: (CrewMember) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${crewMember.name}", fontWeight = FontWeight.Bold)
            Text("Role: ${crewMember.role}")
            Text("Availability: ${crewMember.availability}", color = Color(0xFF004D40))
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { onEditCrewClick(crewMember) }) {
                    Text("Edit")
                }
                Button(onClick = { onDeleteCrewClick(crewMember) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Delete")
                }
            }
        }
    }
}


// Function: AddCrewDialog
// Description: Box for adding a new crew member with fields for name, role, and availability.
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
    val availableRoles = listOf("" +
            "Manager", "Chef", "Sous Chef","Pastry Chef", "Line Cook", "Bartender", "Server", "Dishwasher", "Cashier", "Dishwasher")

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

// Function: EditCrewDialog
// Description: Box for editing an existing crew member's details.
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