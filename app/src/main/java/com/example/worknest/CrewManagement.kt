/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A02
Date : 3/16/2025
File : CrewManagement.kt
*/
package com.example.worknest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
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
import android.content.Intent
import android.net.Uri
import android.content.Context
import androidx.compose.ui.platform.LocalContext

// Data class representing crew member
data class CrewMember(val id: Int, val name: String, var role: String, val availability: String, val linkedInUrl: String?)

class CrewManagement : ComponentActivity() {
    private lateinit var dbManager: DatabaseManager
    private val crewList = mutableStateListOf<CrewMember>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = DatabaseManager(this)

        crewList.addAll(dbManager.getAllCrew())

        setContent {
            CrewListScreen(
                crewList = crewList,
                onAddCrewClick = { name, role, availability, linkedInUrl, onCrewCountChange ->
                    val newCrew = CrewMember(0, name, role, availability, linkedInUrl)
                    dbManager.insertCrew(name, role, availability, linkedInUrl?:"")
                    crewList.add(newCrew)
                    onCrewCountChange()
                    Toast.makeText(this, "$name added", Toast.LENGTH_SHORT).show()
                },
                onDeleteCrewClick = { crewMember, onCrewCountChange ->
                    dbManager.deleteCrew(crewMember.id)
                    crewList.remove(crewMember)
                    onCrewCountChange()
                    Toast.makeText(this, "${crewMember.name} deleted", Toast.LENGTH_SHORT).show()
                },
                onEditCrewClick = { id, newName, newRole, newAvailability, newLinkedInUrl ->
                    dbManager.updateCrew(id, newName, newRole, newAvailability, newLinkedInUrl?:"")
                    val updatedCrew = CrewMember(id, newName, newRole, newAvailability, newLinkedInUrl)
                    val index = crewList.indexOfFirst { it.id == id }
                    if (index != -1) {
                        crewList[index] = updatedCrew
                        Toast.makeText(this, "$newName updated", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

// Function: CrewListScreen
// Description: Displays the list of crew members and provides options to add, edit, or delete them
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrewListScreen(
    crewList: MutableList<CrewMember>,
    onDeleteCrewClick: (CrewMember, () -> Unit) -> Unit,
    onAddCrewClick: (String, String, String, String?, () -> Unit) -> Unit,
    onEditCrewClick: (Int, String, String, String, String?) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var crewMemberToEdit by remember { mutableStateOf<CrewMember?>(null) }
    var selectedCrewMember by remember { mutableStateOf<CrewMember?>(null) }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }
    var linkedInUrl by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("👥 Crew Management", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = Color(0xFF00796B),
                titleContentColor = Color.White
            ),
            actions = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Crew Member", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        )
    }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(crewList) { crewMember ->
                CrewMemberCard(
                    crewMember = crewMember,
                    onDeleteCrewClick = { onDeleteCrewClick(crewMember, {}) },
                    onEditCrewClick = { crewMemberToEdit = it },
                    onLinkedInClick = { linkedInUrl -> openLinkedInProfile(context, linkedInUrl) },
                    onCardClick = { selectedCrewMember = it }
                )
            }
        }
    }

    // Show dialog if a crew member is selected
    selectedCrewMember?.let { crew ->
        AlertDialog(
            onDismissRequest = { selectedCrewMember = null },
            title = { Text("Crew Member Details") },
            text = {
                Column {
                    Text("Name: ${crew.name}", fontWeight = FontWeight.Bold)
                    Text("Role: ${crew.role}")
                    Text("Availability: ${crew.availability}")
                    crew.linkedInUrl?.let {
                        Text("LinkedIn: $it", color = Color.Blue, modifier = Modifier.clickable { openLinkedInProfile(context, it) })
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedCrewMember = null }) { Text("Close") }
            }
        )
    }

    if (showDialog) {
        AddCrewDialog(
            onDismiss = { showDialog = false },
            onAddCrew = { name, role, availability, linkedInUrl ->
                onAddCrewClick(name, role, availability, linkedInUrl) { showDialog = false }
            },
            name = name,
            onNameChange = { name = it }, // Update the state when the name changes
            role = role,
            onRoleChange = { role = it }, // Update the state when the role changes
            availability = availability,
            onAvailabilityChange = { availability = it }, // Update the state when availability changes
            linkedInUrl = linkedInUrl,
            onLinkedInUrlChange = { linkedInUrl = it } // Update the state when LinkedIn URL changes
        )
    }

    // Open Edit Crew Dialog if crewMemberToEdit is not null
    crewMemberToEdit?.let { crew ->
        EditCrewDialog(
            crewMember = crew,
            onDismiss = { crewMemberToEdit = null },
            onEditCrew = { newName, newRole, newAvailability, newLinkedInUrl ->
                onEditCrewClick(crew.id, newName, newRole, newAvailability, newLinkedInUrl)
                crewMemberToEdit = null
            }
        )
    }
}


// Function: CrewMemberCard
// Description: Displays information about a crew member with edit and delete options
@Composable
fun CrewMemberCard(
    crewMember: CrewMember,
    onDeleteCrewClick: (CrewMember) -> Unit,
    onEditCrewClick: (CrewMember) -> Unit,
    onLinkedInClick: (String) -> Unit,
    onCardClick: (CrewMember) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .clickable { onCardClick(crewMember) }, // Trigger onCardClick when card is clicked
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${crewMember.name}", fontWeight = FontWeight.Bold)
            Text("Role: ${crewMember.role}")
            Text("Availability: ${crewMember.availability}", color = Color(0xFF004D40))
            crewMember.linkedInUrl?.let {
                Text("View LinkedIn", color = Color.Blue, modifier = Modifier.clickable { onLinkedInClick(it) })

            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { onEditCrewClick(crewMember) }) { Text("Edit") }
                Button(onClick = { showDeleteDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Delete")
                }
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Crew Member") },
            text = { Text("Are you sure you want to remove this crew member?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCrewClick(crewMember)
                        showDeleteDialog = false // Close the dialog after deletion
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}


// Function: AddCrewDialog
// Description: Box for adding a new crew member with fields for name, role, and availability.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCrewDialog(
    onDismiss: () -> Unit,
    onAddCrew: (String, String, String, String?) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    role: String,
    onRoleChange: (String) -> Unit,
    availability: String,
    onAvailabilityChange: (String) -> Unit,
    linkedInUrl: String,
    onLinkedInUrlChange: (String) -> Unit
) {
    val availableOptions = listOf("Available", "Unavailable")

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
                RolePriorityDropdown(selectedRole = role, onRoleSelected = onRoleChange)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Availability:")
                availableOptions.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = availability == option,
                            onClick = { onAvailabilityChange(option) }
                        )
                        Text(option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = linkedInUrl,
                    onValueChange = onLinkedInUrlChange,
                    label = { Text("LinkedIn URL (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && role.isNotBlank() && availability.isNotBlank()) {
                        onAddCrew(name, role, availability, linkedInUrl)
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
    onEditCrew: (String, String, String, String?) -> Unit
) {
    val availableRoles = listOf("Manager", "Chef", "Sous Chef", "Pastry Chef", "Line Cook", "Bartender", "Server", "Dishwasher", "Cashier")
    val availableOptions = listOf("Available", "Unavailable")

    var name by remember { mutableStateOf(crewMember.name) }
    var role by remember { mutableStateOf(crewMember.role) }
    var availability by remember { mutableStateOf(crewMember.availability) }
    var linkedInUrl by remember { mutableStateOf(crewMember.linkedInUrl ?: "") }

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
                RolePriorityDropdown(selectedRole = role, onRoleSelected = { role = it })

                Spacer(modifier = Modifier.height(8.dp))
                Text("Availability:")
                availableOptions.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = availability == option,
                            onClick = { availability = option }
                        )
                        Text(option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = linkedInUrl,
                    onValueChange = { linkedInUrl = it },
                    label = { Text("LinkedIn URL (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && role.isNotBlank() && availability.isNotBlank()) {
                        onEditCrew(name, role, availability, linkedInUrl)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolePriorityDropdown(selectedRole: String, onRoleSelected: (String) -> Unit) {
    val roles = listOf("Manager", "Chef", "Sous Chef", "Pastry Chef", "Line Cook", "Bartender", "Server", "Dishwasher", "Cashier")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedRole,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Role") },
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
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role) },
                    onClick = {
                        onRoleSelected(role)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun openLinkedInProfile(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}
