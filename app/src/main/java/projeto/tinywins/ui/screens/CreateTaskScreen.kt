package projeto.tinywins.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*
import projeto.tinywins.data.*
import projeto.tinywins.ui.viewmodel.CreateTaskUiState
import projeto.tinywins.ui.viewmodel.CreateTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(onDismissRequest: () -> Unit, onConfirm: (Calendar) -> Unit, title: String = "Selecione o Horário") {
    val timeState = rememberTimePickerState(is24Hour = true)
    Dialog(onDismissRequest = onDismissRequest) {
        Card(shape = MaterialTheme.shapes.extraLarge) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                TimePicker(state = timeState)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, timeState.hour)
                        cal.set(Calendar.MINUTE, timeState.minute)
                        cal.isLenient = false
                        onConfirm(cal)
                    }) { Text("OK") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    navController: NavHostController,
    viewModel: CreateTaskViewModel
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedTaskType by remember { mutableStateOf(TaskType.HABIT) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    var selectedFrequency by remember { mutableStateOf(ResetFrequency.DAILY) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val checklistItems = remember { mutableStateListOf<ChecklistItem>() }
    val reminders = remember { mutableStateListOf<Long>() }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(ChallengeCategory.PRODUTIVIDADE) }
    var isPositiveHabit by remember { mutableStateOf(true) }
    var isNegativeHabit by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is CreateTaskUiState.Success) {
            navController.popBackStack()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val timeZone = TimeZone.getDefault()
                        val offset = timeZone.getOffset(Date().time)
                        selectedDateMillis = it + offset
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(onDismissRequest = { showTimePicker = false }, onConfirm = { calendar ->
            val finalDateTime = Calendar.getInstance()
            selectedDateMillis?.let { finalDateTime.timeInMillis = it }
            finalDateTime.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            finalDateTime.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
            reminders.add(finalDateTime.timeInMillis)
            showTimePicker = false
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar ${if (selectedTaskType == TaskType.HABIT) "Hábito" else "To Do"}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    val isLoading = uiState is CreateTaskUiState.Loading
                    TextButton(
                        onClick = {
                            val xpReward = when (selectedDifficulty) {
                                Difficulty.TRIVIAL -> 5; Difficulty.EASY -> 15; Difficulty.MEDIUM -> 25; Difficulty.HARD -> 40
                            }
                            val coinReward = when (selectedDifficulty) {
                                Difficulty.TRIVIAL -> 5; Difficulty.EASY -> 10; Difficulty.MEDIUM -> 20; Difficulty.HARD -> 30
                            }
                            val newChallenge = TinyWinChallenge(
                                title = title,
                                description = notes,
                                type = selectedTaskType,
                                xp = xpReward,
                                coins = coinReward,
                                difficulty = selectedDifficulty,
                                isPositive = if (selectedTaskType == TaskType.TODO) true else isPositiveHabit,
                                isNegative = if (selectedTaskType == TaskType.TODO) false else isNegativeHabit,
                                resetFrequency = if (selectedTaskType == TaskType.HABIT) selectedFrequency else null,
                                dueDate = if (selectedTaskType == TaskType.TODO) selectedDateMillis else null,
                                checklist = if (selectedTaskType == TaskType.TODO) checklistItems.toList() else emptyList(),
                                reminders = reminders.toList(),
                                category = selectedCategory
                            )
                            viewModel.createChallenge(newChallenge)
                        },
                        enabled = title.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("CRIAR", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
            TabRow(selectedTabIndex = selectedTaskType.ordinal) {
                listOf("Hábito", "To Do").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTaskType.ordinal == index,
                        onClick = { selectedTaskType = TaskType.entries[index] },
                        text = { Text(title) }
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth().height(120.dp))
                Spacer(modifier = Modifier.height(24.dp))

                Text("Categoria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    ChallengeCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name.lowercase().replaceFirstChar { it.titlecase() }) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text("Dificuldade", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { difficulty ->
                        DifficultyButton(difficulty = difficulty, isSelected = selectedDifficulty == difficulty, onClick = { selectedDifficulty = difficulty }, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (selectedTaskType == TaskType.HABIT) {
                    HabitOptions(
                        isPositive = isPositiveHabit,
                        onPositiveChange = { isPositiveHabit = it },
                        isNegative = isNegativeHabit,
                        onNegativeChange = { isNegativeHabit = it },
                        selectedFrequency = selectedFrequency,
                        onFrequencyChange = { selectedFrequency = it }
                    )
                } else {
                    TodoOptions(selectedDateMillis, { showDatePicker = true }, checklistItems, { checklistItems.add(ChecklistItem(text = "")) }, { index, newText -> checklistItems[index] = checklistItems[index].copy(text = newText) }, { index -> checklistItems.removeAt(index) }, reminders, { showTimePicker = true }, { index -> reminders.removeAt(index) })
                }
            }
        }
    }
}

@Composable
private fun HabitOptions(
    isPositive: Boolean,
    onPositiveChange: (Boolean) -> Unit,
    isNegative: Boolean,
    onNegativeChange: (Boolean) -> Unit,
    selectedFrequency: ResetFrequency,
    onFrequencyChange: (ResetFrequency) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Text("Tipo de Interação", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = isPositive, onClick = { onPositiveChange(!isPositive) }, label = { Text("+ Positivo") })
                FilterChip(selected = isNegative, onClick = { onNegativeChange(!isNegative) }, label = { Text("- Negativo") })
            }
        }
        Column {
            Text("Reiniciar Contador", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ResetFrequency.entries.forEach { frequency ->
                    FilterChip(selected = selectedFrequency == frequency, onClick = { onFrequencyChange(frequency) }, label = { Text(frequency.toPortuguese()) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoOptions(selectedDateMillis: Long?, onDateClick: () -> Unit, checklistItems: SnapshotStateList<ChecklistItem>, onChecklistItemAdded: () -> Unit, onChecklistItemChanged: (Int, String) -> Unit, onChecklistItemRemoved: (Int) -> Unit, reminders: List<Long>, onAddReminderClick: () -> Unit, onRemoveReminderClick: (Int) -> Unit) {
    Column {
        Text("Agendamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.clickable(onClick = onDateClick)) {
            OutlinedTextField(value = selectedDateMillis?.let { formatDate(it) } ?: "Selecione uma data", onValueChange = {}, enabled = false, label = { Text("Data de Entrega") }, trailingIcon = { Icon(Icons.Default.CalendarToday, "Selecionar Data") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant, disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        checklistItems.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = item.text, onValueChange = { newText -> onChecklistItemChanged(index, newText) }, modifier = Modifier.weight(1f), label = { Text("Item ${index + 1}") })
                IconButton(onClick = { onChecklistItemRemoved(index) }) { Icon(Icons.Default.Close, contentDescription = "Remover Item") }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        TextButton(onClick = onChecklistItemAdded) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Item", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Adicionar item")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Lembretes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        reminders.forEachIndexed { index, millis ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = formatTime(millis), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { onRemoveReminderClick(index) }) { Icon(Icons.Default.Close, contentDescription = "Remover Lembrete") }
            }
        }
        TextButton(onClick = onAddReminderClick) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Lembrete", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Adicionar lembrete")
        }
    }
}

private fun formatDate(millis: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
private fun formatTime(millis: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))

@Composable
private fun DifficultyButton(difficulty: Difficulty, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row {
                repeat(difficulty.ordinal + 1) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
            }
            Text(text = difficulty.toPortuguese(), style = MaterialTheme.typography.labelMedium, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}