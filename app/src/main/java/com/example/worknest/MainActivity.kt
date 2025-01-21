package com.example.worknest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkNestApp()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkNestApp() {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = { Text("WorkNest") }) },
        content = {
            CrewListScreen()
        }
    )
}

@Composable
fun CrewListScreen() {
    val crewList = listOf("Crew Member 1", "Crew Member 2", "Crew Member 3", "Crew Member 4")

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(crewList) { crewMember ->
            CrewMemberItem(crewMember)
        }
    }
}

@Composable
fun CrewMemberItem(name: String) {
    Text(text = name, modifier = Modifier.padding(16.dp))
}
