package com.example.worknest

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

data class Expense(val name: String, val category: String, val amount: Double, val date: String)

class ExpenseScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShowExpenses()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowExpenses() {
    var expenses by remember { mutableStateOf(CurrentExpenses()) }
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Expenses") }) },
        floatingActionButton = {        //Button to add a new expense
            FloatingActionButton(
                onClick = { showDialog = true },
                content = { Text("+") }
            )
        }
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
    if (showDialog) {
        AddExpenseDialog(
            onAddExpense = { newExpense ->
                expenses = expenses + newExpense
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

}

@Composable
fun AddExpenseDialog(onAddExpense: (Expense) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> date = "$year-${month + 1}-$dayOfMonth" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Expense Name") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
                Spacer(modifier = Modifier.height(8.dp))

                // Date Picker Button
                Button(onClick = { datePickerDialog.show() }) {
                    Text(if (date.isEmpty()) "Pick Date" else "Date: $date")
                }
                Spacer(modifier = Modifier.height(8.dp))

                //Image Picker

            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotEmpty() && category.isNotEmpty() && amount.isNotEmpty() && date.isNotEmpty()) {
                    onAddExpense(Expense(name, category, amount.toDouble(), date))
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
        HorizontalDivider()

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


