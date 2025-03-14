    /*
    Students Name : Jyot Shah & Ashwini Gunaga
    Students Number : 8871717 & 8888180
    Assignment : A01
    Date : 2/13/2025
    File : ExpenseScreen.kt
    */

    package com.example.worknest

    import android.content.ContentValues
    import android.content.Context
    import android.net.Uri
    import android.os.Build
    import android.os.Bundle
    import android.os.Environment
    import android.provider.MediaStore
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
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.sp
    import com.example.worknest.database.DatabaseManager
    import com.example.worknest.models.Expense
    import java.util.Calendar
    import android.widget.Toast
    import java.io.OutputStream

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
        val context = LocalContext.current
        val dbManager = remember { DatabaseManager(context) }

        var expenses by remember { mutableStateOf(dbManager.getAllExpenses()) }
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
        )
        { paddingValues ->
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
                    dbManager.insertExpense(newExpense.name, newExpense.category, newExpense.amount, newExpense.date)
                    expenses = dbManager.getAllExpenses() // Refresh list
                    showDialog = false
                    Toast.makeText(context, "Expense added successfully!", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showDialog = false }
            )
        }
    }

    @Composable
    fun AddExpenseDialog(onAddExpense: (Expense) -> Unit, onDismiss: () -> Unit) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf(50f) }
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
            title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Expense Name") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                    Spacer(modifier = Modifier.height(8.dp))
                    AmountSlider(amount, onAmountChange = { amount = it })
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { datePickerDialog.show() }) {
                        Text(if (date.isEmpty()) "\uD83D\uDCC6 Pick Date" else "\uD83D\uDCC6 Date: $date")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotEmpty() && category.isNotEmpty() && date.isNotEmpty()) {
                        onAddExpense(Expense(0, name, category, amount.toDouble(), date))
                    } else {
                        Toast.makeText(context, "⚠️ All fields are required!", Toast.LENGTH_SHORT).show()

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
    fun AmountSlider(amount: Float, onAmountChange: (Float) -> Unit) {
        Column {
            Text(
                "\uD83D\uDCB5 Amount: $${"%.2f".format(amount)}",
                modifier = Modifier.padding(8.dp)
            )
            Slider(
                value = amount,
                onValueChange = onAmountChange,
                valueRange = 0f..1000f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00796B),
                    activeTrackColor = Color(0xFF004D40)
                )
            )
        }
    }

    @Composable
    fun ExpenseTable(expenses: List<Expense>) {
        val context = LocalContext.current
        Button(onClick = { saveExpensesToFile(context, expenses) }) {
            Text("Download Expenses 📥")
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color(0xFF00796B)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeader("📝 Expense", Modifier.weight(2f))
                TableHeader("📂 Category", Modifier.weight(1.5f))
                TableHeader("\uD83D\uDCB5 Amount", Modifier.weight(1.5f))
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
            Text(
                text = "$${"%.2f".format(expense.amount)}", // Ensures 2 decimal places
                modifier = Modifier.weight(1.5f),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00796B)
            )
            Text(text = expense.date, modifier = Modifier.weight(1.2f), color = Color.Gray)
        }
    }

    fun saveExpensesToFile(context: Context, expenses: List<Expense>) {
        val fileName = "expenses.csv"
        val fileContent = buildCsv(expenses)

        val resolver = context.contentResolver
        var outputStream: OutputStream? = null

        try {
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ (Scoped Storage)
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                // Android 9 and below (Legacy Storage)
                val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .resolve(fileName)
                file.outputStream()
            }

            outputStream = uri?.let { resolver.openOutputStream(it as Uri) }
            outputStream?.write(fileContent.toByteArray())

            Toast.makeText(context, "Expenses saved to Downloads folder", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
        } finally {
            outputStream?.close()
        }
    }

    fun buildCsv(expenses: List<Expense>): String {
        val builder = StringBuilder()
        builder.append("Expense Name,Category,Amount,Date\n") // CSV Header

        for (expense in expenses) {
            builder.append("${expense.name},${expense.category},${expense.amount},${expense.date}\n")
        }

        return builder.toString()
    }





