package projeto.tinywins.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import projeto.tinywins.data.ChallengeCategory
import projeto.tinywins.data.TaskType
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.data.toColor
import projeto.tinywins.data.toIcon
import projeto.tinywins.ui.theme.TinyWinsTheme

@Composable
fun ChallengeItemCard(
    challenge: TinyWinChallenge,
    onClick: () -> Unit,
    onPositiveAction: () -> Unit,
    onNegativeAction: () -> Unit,
    onTodoChecked: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado local para controlar o checkbox de um TODO
    var isTodoChecked by remember(challenge.id, challenge.isCompleted) { mutableStateOf(challenge.isCompleted) }

    val cardAlpha = if (isTodoChecked && challenge.type == TaskType.TODO) 0.6f else 1f
    val textDecoration = if (isTodoChecked && challenge.type == TaskType.TODO) TextDecoration.LineThrough else TextDecoration.None

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = cardAlpha)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            // Lógica para mostrar interação de HÁBITO (+)
            if (challenge.type == TaskType.HABIT) {
                IconButton(onClick = onPositiveAction, modifier = Modifier.padding(start = 4.dp)) {
                    Icon(Icons.Default.Add, "Positivo", tint = MaterialTheme.colorScheme.primary)
                }
            }
            // Para TODOs, mostramos um Checkbox
            if (challenge.type == TaskType.TODO) {
                Checkbox(
                    checked = isTodoChecked,
                    onCheckedChange = {
                        isTodoChecked = it
                        onTodoChecked(it)
                    },
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            // Conteúdo Central (Ícone, Título, XP)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = challenge.category.toIcon,
                    contentDescription = "Categoria",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = challenge.category.toColor().copy(alpha = cardAlpha)
                )
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textDecoration = textDecoration,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = cardAlpha)
                    )
                    Text(
                        text = "${challenge.xp} XP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFFC107).copy(alpha = cardAlpha),
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = textDecoration
                    )
                }
            }

            // Lógica para mostrar interação de HÁBITO (-)
            if (challenge.type == TaskType.HABIT) {
                IconButton(onClick = onNegativeAction, modifier = Modifier.padding(end = 4.dp)) {
                    Icon(Icons.Default.Remove, "Negativo", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(name = "Habit Card Preview")
@Composable
private fun HabitItemCardPreview() {
    val habit = sampleChallenges.first { it.type == TaskType.HABIT }
    TinyWinsTheme(useDarkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            ChallengeItemCard(
                challenge = habit,
                onClick = {},
                onPositiveAction = {},
                onNegativeAction = {},
                onTodoChecked = {}
            )
        }
    }
}

@Preview(name = "TODO Card Preview")
@Composable
private fun TodoItemCardPreview() {
    val todo = sampleChallenges.first { it.type == TaskType.TODO }
    TinyWinsTheme(useDarkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            ChallengeItemCard(
                challenge = todo,
                onClick = {},
                onPositiveAction = {},
                onNegativeAction = {},
                onTodoChecked = {}
            )
        }
    }
}