package projeto.tinywins.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import projeto.tinywins.data.ChallengeCategory // IMPORTAÇÃO ADICIONADA
import projeto.tinywins.data.ChecklistItem
import projeto.tinywins.data.Difficulty
import projeto.tinywins.data.ResetFrequency
import projeto.tinywins.data.TaskType
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.ui.theme.TinyWinsTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedTaskType by remember { mutableStateOf(TaskType.HABIT) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    var isPositive by remember { mutableStateOf(true) }
    var isNegative by remember { mutableStateOf(false) }
    var selectedFrequency by remember { mutableStateOf(ResetFrequency.DAILY) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val checklistItems = remember { mutableStateListOf<ChecklistItem>() }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis
                        if (selectedDate != null) {
                            val timeZone = TimeZone.getDefault()
                            val offset = timeZone.getOffset(Date().time)
                            selectedDateMillis = selectedDate + offset
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
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
                    TextButton(
                        onClick = {
                            val xpReward = when(selectedDifficulty) {
                                Difficulty.TRIVIAL -> 5
                                Difficulty.EASY -> 15
                                Difficulty.MEDIUM -> 25
                                Difficulty.HARD -> 40
                            }
                            val coinReward = when(selectedDifficulty) {
                                Difficulty.TRIVIAL -> 5
                                Difficulty.EASY -> 10
                                Difficulty.MEDIUM -> 20
                                Difficulty.HARD -> 30
                            }
                            val newChallenge = TinyWinChallenge(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                description = notes,
                                type = selectedTaskType,
                                xp = xpReward,
                                coins = coinReward,
                                difficulty = selectedDifficulty,
                                isPositive = if (selectedTaskType == TaskType.HABIT) isPositive else true,
                                isNegative = if (selectedTaskType == TaskType.HABIT) isNegative else false,
                                resetFrequency = if (selectedTaskType == TaskType.HABIT) selectedFrequency else null,
                                dueDate = if (selectedTaskType == TaskType.TODO) selectedDateMillis else null,
                                checklist = if (selectedTaskType == TaskType.TODO) checklistItems.toList() else emptyList(),
                                category = ChallengeCategory.PRODUTIVIDADE,
                                imageResId = null
                            )
                            sampleChallenges.add(0, newChallenge)
                            navController.popBackStack()
                        },
                        enabled = title.isNotBlank()
                    ) { Text("CRIAR", fontWeight = FontWeight.Bold) }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            val tabTitles = listOf("Hábito", "To Do")
            TabRow(selectedTabIndex = selectedTaskType.ordinal) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTaskType.ordinal == index,
                        onClick = { selectedTaskType = TaskType.entries[index] },
                        text = { Text(title) }
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Dificuldade", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Difficulty.entries.forEach { difficulty ->
                        DifficultyButton(
                            difficulty = difficulty,
                            isSelected = selectedDifficulty == difficulty,
                            onClick = { selectedDifficulty = difficulty },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                if (selectedTaskType == TaskType.HABIT) {
                    HabitOptions(
                        isPositive = isPositive,
                        onPositiveChange = { isPositive = it },
                        isNegative = isNegative,
                        onNegativeChange = { isNegative = it },
                        selectedFrequency = selectedFrequency,
                        onFrequencyChange = { selectedFrequency = it }
                    )
                } else {
                    TodoOptions(
                        selectedDateMillis = selectedDateMillis,
                        onDateClick = { showDatePicker = true },
                        checklistItems = checklistItems,
                        onChecklistItemAdded = { checklistItems.add(ChecklistItem(text = "")) },
                        onChecklistItemChanged = { index, newText -> checklistItems[index] = checklistItems[index].copy(text = newText) },
                        onChecklistItemRemoved = { index -> checklistItems.removeAt(index) }
                    )
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
    Column {
        Text("Tipo de Interação", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isPositive, onClick = { onPositiveChange(!isPositive) }, label = { Text("+ Positivo") })
            FilterChip(selected = isNegative, onClick = { onNegativeChange(!isNegative) }, label = { Text("- Negativo") })
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Reiniciar Contador", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ResetFrequency.entries.forEach { frequency ->
                FilterChip(
                    selected = selectedFrequency == frequency,
                    onClick = { onFrequencyChange(frequency) },
                    label = { Text(frequency.name.lowercase().replaceFirstChar { it.titlecase() }) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoOptions(
    selectedDateMillis: Long?,
    onDateClick: () -> Unit,
    checklistItems: SnapshotStateList<ChecklistItem>,
    onChecklistItemAdded: () -> Unit,
    onChecklistItemChanged: (Int, String) -> Unit,
    onChecklistItemRemoved: (Int) -> Unit
) {
    Column {
        Text("Agendamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.clickable(onClick = onDateClick)) {
            OutlinedTextField(
                value = selectedDateMillis?.let { formatDate(it) } ?: "Selecione uma data",
                onValueChange = {},
                enabled = false,
                label = { Text("Data de Entrega") },
                trailingIcon = { Icon(Icons.Default.CalendarToday, "Selecionar Data") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        checklistItems.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = item.text,
                    onValueChange = { newText -> onChecklistItemChanged(index, newText) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Item ${index + 1}") }
                )
                IconButton(onClick = { onChecklistItemRemoved(index) }) {
                    Icon(Icons.Default.Close, contentDescription = "Remover Item")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        TextButton(onClick = onChecklistItemAdded) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Item", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Adicionar item")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("TODO: Lembretes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
private fun DifficultyButton(
    difficulty: Difficulty,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row {
                repeat(difficulty.ordinal + 1) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = difficulty.name.lowercase().replaceFirstChar { it.titlecase() },
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTaskScreenPreview() {
    TinyWinsTheme(useDarkTheme = true) {
        val navController = rememberNavController()
        CreateTaskScreen(navController = navController)
    }
}