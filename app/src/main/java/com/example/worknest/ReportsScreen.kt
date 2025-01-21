package com.example.worknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class ReportsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReportsFormScreen()
        }
    }
}

@Composable
fun ReportsFormScreen() {
    var selectedDateRange by remember { mutableStateOf("Last Week") }
    var workReport by remember { mutableStateOf("No Report Available") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Work Reports", style = MaterialTheme.typography.titleMedium)

        Text("Select Date Range:")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // Action to generate report
            workReport = "Generated report for $selectedDateRange"
        }) {
            Text("Generate Report")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(workReport)
    }
}
