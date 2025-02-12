package com.example.worknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

data class Expense(val name: String, val category: String, val amount: Double, val date: String)

class ExpenseScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShowExpenses(expenses = CurrentExpenses())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowExpenses(expenses : List<Expense>) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Expenses") }) }
        )  { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        )   {
            ExpenseTable(expenses)
            }
         }
    }

@Composable
fun ExpenseTable(expenses: List<Expense>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Table Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableHeader("Expense")
            TableHeader("Category")
            TableHeader("Amount")
            TableHeader("Date")
        }
        Divider()

        // Table Rows
        LazyColumn {
            items(expenses) { expense ->
                ExpenseRow(expense)
            }
        }
    }
}

@Composable
fun TableHeader(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}

@Composable
fun ExpenseRow(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = expense.name, modifier = Modifier.weight(1f))
        Text(text = expense.category, modifier = Modifier.weight(1f))
        Text(text = "$${expense.amount}", modifier = Modifier.weight(1f))
        Text(text = expense.date, modifier = Modifier.weight(1f))
    }
}


fun CurrentExpenses(): List<Expense> {
    return listOf(
        Expense("Produce", "Food", 500.0, "2025-02-10"),
        Expense("Dairy", "Beverages", 300.0, "2025-02-11")
    )
}


