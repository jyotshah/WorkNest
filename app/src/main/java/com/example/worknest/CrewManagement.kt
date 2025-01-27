package com.example.worknest

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class CrewMember(val id: Int, val name: String, val role: String, val availability: String)

class CrewManagement : ComponentActivity() {
    private val crewList = mutableStateListOf<CrewMember>(
        CrewMember(1, "Alice", "Pilot", "Available"),
        CrewMember(2, "Bob", "Engineer", "Unavailable")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrewListScreen(
                crewList = crewList,
                onAddCrewClick = {
                    val intent = Intent(this, AddEditCrewActivity::class.java)
                    startActivityForResult(intent, ADD_CREW_REQUEST)
                },
                onEditCrewClick = { crewMember ->
                    val intent = Intent(this, AddEditCrewActivity::class.java).apply {
                        putExtra("crewId", crewMember.id)
                        putExtra("crewName", crewMember.name)
                        putExtra("crewRole", crewMember.role)
                        putExtra("crewAvailability", crewMember.availability)
                    }
                    startActivityForResult(intent, EDIT_CREW_REQUEST)
                },
                onDeleteCrewClick = { crewMember ->
                    crewList.remove(crewMember)
                    Toast.makeText(this, "${crewMember.name} deleted", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }


override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK) {
        val name = data?.getStringExtra("crewName") ?: ""
        val role = data?.getStringExtra("crewRole") ?: ""
        val availability = data?.getStringExtra("crewAvailability") ?: ""
        val crewId = data?.getIntExtra("crewId", -1) ?: -1

        if (requestCode == ADD_CREW_REQUEST) {
            // Changed: Handle adding new crew member
            val newCrewMember = CrewMember(
                id = crewList.size + 1,
                name = name,
                role = role,
                availability = availability
            )
            crewList.add(newCrewMember)
            Toast.makeText(this, "Crew Member Added", Toast.LENGTH_SHORT).show()
        } else if (requestCode == EDIT_CREW_REQUEST && crewId != -1) {

            val crewMemberIndex = crewList.indexOfFirst { it.id == crewId }
            if (crewMemberIndex != -1) {
                val updatedCrew = crewList[crewMemberIndex].copy(name = name, role = role, availability = availability)
                crewList[crewMemberIndex] = updatedCrew // Changed: Update crew member in the list
                Toast.makeText(this, "Crew Member Updated", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
companion object {
    const val ADD_CREW_REQUEST = 1
    const val EDIT_CREW_REQUEST = 2
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrewListScreen(
    crewList: List<CrewMember>,
    onAddCrewClick: () -> Unit,
    onEditCrewClick: (CrewMember) -> Unit,
    onDeleteCrewClick: (CrewMember) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Crew Management") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCrewClick) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.padding(16.dp)
        ) {
            items(crewList) { crewMember ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onEditCrewClick(crewMember) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${crewMember.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Role: ${crewMember.role}", style = MaterialTheme.typography.bodyLarge)
                        Text("Availability: ${crewMember.availability}", style = MaterialTheme.typography.bodySmall)

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
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
        }
    }
}

class AddEditCrewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crewId = intent.getIntExtra("crewId", -1)
        val crewName = intent.getStringExtra("crewName") ?: ""
        val crewRole = intent.getStringExtra("crewRole") ?: ""
        val crewAvailability = intent.getStringExtra("crewAvailability") ?: ""

        setContent {
            AddEditCrewFormScreen(
                crewId = crewId,
                crewName = crewName,
                crewRole = crewRole,
                crewAvailability = crewAvailability,
                onSaveCrew = { name, role, availability ->
                    val intent = Intent().apply {
                        putExtra("crewName", name)
                        putExtra("crewRole", role)
                        putExtra("crewAvailability", availability)
                        if (crewId != -1) {
                            putExtra("crewId", crewId)
                        }
                    }
                    setResult(RESULT_OK, intent)
                    Toast.makeText(this, "Crew Member Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
            )
        }
    }
}

@Composable
fun AddEditCrewFormScreen(
    crewId: Int,
    crewName: String,
    crewRole: String,
    crewAvailability: String,
    onSaveCrew: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(crewName) }
    var role by remember { mutableStateOf(crewRole) }
    var availability by remember { mutableStateOf(crewAvailability) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            if (crewId == -1) "Add New Crew Member" else "Edit Crew Member",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = role,
            onValueChange = { role = it },
            label = { Text("Role") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = availability,
            onValueChange = { availability = it },
            label = { Text("Availability") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSaveCrew(name, role, availability) }) {
            Text("Save Crew Member")
        }
    }
}