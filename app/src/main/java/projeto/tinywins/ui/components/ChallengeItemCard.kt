package projeto.tinywins.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    modifier: Modifier = Modifier,
    onPositiveAction: (() -> Unit)? = null,
    onNegativeAction: (() -> Unit)? = null,
    onTodoChecked: ((Boolean) -> Unit)? = null,
    onUnfavoriteClick: (() -> Unit)? = null
) {
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
            // Lógica modificada para o ícone da esquerda
            if (onUnfavoriteClick == null) { // Só mostra o ícone esquerdo se NÃO for a tela de favoritos
                when {
                    challenge.type == TaskType.HABIT && onPositiveAction != null -> {
                        IconButton(onClick = onPositiveAction, modifier = Modifier.padding(start = 4.dp)) {
                            Icon(Icons.Default.Add, "Positivo", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    challenge.type == TaskType.TODO && onTodoChecked != null -> {
                        Checkbox(
                            checked = isTodoChecked,
                            onCheckedChange = {
                                isTodoChecked = it
                                onTodoChecked(it)
                            },
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = challenge.category.toIcon,
                            contentDescription = "Categoria",
                            modifier = Modifier.padding(start = 16.dp),
                            tint = challenge.category.toColor().copy(alpha = cardAlpha)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp)) // Espaço para alinhar o texto
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp, horizontal = 12.dp)
            ) {
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

            // O ícone de unfavorite (coração partido) permanece à direita
            if (onUnfavoriteClick != null) {
                IconButton(onClick = onUnfavoriteClick, modifier = Modifier.padding(end = 4.dp)) {
                    Icon(Icons.Default.HeartBroken, "Remover Favorito", tint = MaterialTheme.colorScheme.error)
                }
            } else if (challenge.type == TaskType.HABIT && onNegativeAction != null) {
                IconButton(onClick = onNegativeAction, modifier = Modifier.padding(end = 4.dp)) {
                    Icon(Icons.Default.Remove, "Negativo", tint = MaterialTheme.colorScheme.error)
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
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