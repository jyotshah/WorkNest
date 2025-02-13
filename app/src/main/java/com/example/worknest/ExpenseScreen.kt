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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.Calendar

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

// Amount Slider Component
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

@Composable
fun ExpenseTable(expenses: List<Expense>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color(0xFF00796B)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableHeader("📝 Expense", Modifier.weight(2f))
            TableHeader("📂 Category", Modifier.weight(1.5f))
            TableHeader("\uD83D\uDCB5 Amount", Modifier.weight(1f))
            TableHeader("\uD83D\uDCC6 Date", Modifier.weight(1.2f))
        }

        Divider(color = Color.Gray, thickness = 2.dp)

        LazyColumn {
            items(expenses) { expense ->
                ExpenseRow(expense)
            }
        }
    }
}

@Composable
fun TableHeader(text: String, modifier: Modifier) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun ExpenseRow(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = expense.name, modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
        Text(text = expense.category, modifier = Modifier.weight(1.5f), color = Color(0xFF004D40))
        Text(text = "$${expense.amount}", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
        Text(text = expense.date, modifier = Modifier.weight(1.2f), color = Color.Gray)
    }
}


fun currentExpenses(): List<Expense> {
    return listOf(
        Expense("Produce", "Food", 500.0, "2025-02-10"),
        Expense("Dairy", "Beverages", 300.0, "2025-02-11")
    )
}


