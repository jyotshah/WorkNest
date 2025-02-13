/*
Students Name : Jyot Shah & Ashwini Gunaga
Students Number : 8871717 & 8888180
Assignment : A01
Date : 2/13/2025
File : ExpenseScreen.kt
*/
package com.example.worknest

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.Calendar
// Function: Expense
// Description: Data class to represent an expense with name, category, amount, and date.
data class Expense(val name: String, val category: String, val amount: Double, val date: String)

class ExpenseScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ShowExpenses()
            }
        }
    }
}

// Function name: ShowExpenses
// Function description: Displays the list of expenses and a floating button to add a new expense.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowExpenses() {
    var expenses by rememberSaveable { mutableStateOf(currentExpenses().toMutableList()) }
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("\uD83D\uDCB0 Expense Tracker", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF00796B),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF00796B),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB))
                    )
                )
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            ExpenseTable(expenses)
        }
    }
    if (showDialog) {
        AddExpenseDialog(
            onAddExpense = { newExpense ->
                expenses.add(newExpense)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

}

// Function name: AddExpenseDialog
// Function description: Displays a dialog to input a new expense, including name, category, amount, date, and receipt.
@Composable
fun AddExpenseDialog(onAddExpense: (Expense) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf(50f) } // Default slider value
    var date by remember { mutableStateOf("") }

    var receiptUri by remember { mutableStateOf<Uri?>(null) }

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
        title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Expense Name") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                Spacer(modifier = Modifier.height(8.dp))
                // Amount Slider
                AmountSlider(amount, onAmountChange = { amount = it })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
                Spacer(modifier = Modifier.height(8.dp))

                // Date Picker Button
                Button(onClick = { datePickerDialog.show() }) {
                    Text(if (date.isEmpty()) "\uD83D\uDCC6 Pick Date" else "\uD83D\uDCC6 Date: $date")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotEmpty() && category.isNotEmpty() && date.isNotEmpty()) {
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

// Function name: AmountSlider
// Function description: Displays a slider for setting the amount of the expense.
@Composable
fun AmountSlider(amount: Float, onAmountChange: (Float) -> Unit) {
    Column {
        Text("\uD83D\uDCB5 Amount: $${amount.toInt()}", modifier = Modifier.padding(8.dp))
        Slider(
            value = amount,
            onValueChange = onAmountChange,
            valueRange = 0f..1000f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF00796B),
                activeTrackColor = Color(0xFF004D40)
            )
        )
    }
}

// Function name: ExpenseTable
// Function description: Displays a table with expense data including expense name, category, amount, and date.
@Composable
fun ExpenseTable(expenses: List<Expense>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color(0xFF00796B)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableHeader("📝 Expense", Color.White)
            TableHeader("📂 Category", Color.White)
            TableHeader("💰 Amount", Color.White)
            TableHeader("📅 Date", Color.White)
        }

        Divider(color = Color.Gray, thickness = 2.dp)

        // Expense Rows
        LazyColumn {
            items(expenses) { expense ->
                ExpenseRow(expense)
            }
        }
    }
}

// Function name: TableHeader
// Function description: Displays a header in the expense table.
@Composable
fun TableHeader(text: String, color:Color) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier
            .padding(8.dp)
    )
}

// Function name: ExpenseRow
// Function description: Displays a row representing a single expense in the expense table.
@Composable
fun ExpenseRow(expense: Expense) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = expense.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text(text = expense.category, modifier = Modifier.weight(1f), color = Color(0xFF004D40))
            Text(text = "$${expense.amount}", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
            Text(text = expense.date, modifier = Modifier.weight(1f), color = Color.Gray)
        }
    }
}

// Function: currentExpenses
// Description: Provides a default list of expenses.
fun currentExpenses(): List<Expense> {
    return listOf(
        Expense("Produce", "Food", 500.0, "2025-02-10"),
        Expense("Dairy", "Beverages", 300.0, "2025-02-11")
    )
}


