package projeto.tinywins.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import projeto.tinywins.data.Difficulty
import projeto.tinywins.data.TaskType
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.toColor
import projeto.tinywins.data.toIcon

@Composable
fun ChallengeItemCard(
    challenge: TinyWinChallenge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shouldAnimate: Boolean = false,
    onPositiveAction: (() -> Unit)? = null,
    onNegativeAction: (() -> Unit)? = null,
    onTodoChecked: ((Boolean) -> Unit)? = null
) {
    var isTodoChecked by remember(challenge.id, challenge.isCompleted) { mutableStateOf(challenge.isCompleted) }
    val cardAlpha = if (isTodoChecked && challenge.type == TaskType.TODO) 0.6f else 1f
    val textDecoration = if (isTodoChecked && challenge.type == TaskType.TODO) TextDecoration.LineThrough else TextDecoration.None

    val scale by animateFloatAsState(
        targetValue = if (shouldAnimate) 1.05f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(MaterialTheme.shapes.medium)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = cardAlpha)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Área Clicável da Esquerda (Negativo)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
                    .clickable(
                        enabled = challenge.isNegative && onNegativeAction != null,
                        onClick = { onNegativeAction?.invoke() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (challenge.type == TaskType.HABIT && challenge.isNegative) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Negativo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Área Central Clicável (Detalhes)
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .clickable(onClick = onClick)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = challenge.category.toIcon,
                    contentDescription = "Categoria",
                    tint = challenge.category.toColor()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = textDecoration,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = cardAlpha)
                )
                Text(
                    text = "${challenge.xp} XP | ${challenge.coins} Moedas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = cardAlpha),
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = textDecoration
                )
                DifficultyIndicator(difficulty = challenge.difficulty)
            }

            // Área Clicável da Direita (Positivo / Checkbox)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
                    .clickable(
                        enabled = (challenge.isPositive && onPositiveAction != null) || (onTodoChecked != null && !isTodoChecked),
                        onClick = {
                            if (challenge.type == TaskType.HABIT) {
                                onPositiveAction?.invoke()
                            } else if (challenge.type == TaskType.TODO && !isTodoChecked) {
                                isTodoChecked = true
                                onTodoChecked?.invoke(true)
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (challenge.type) {
                    TaskType.HABIT -> {
                        if (challenge.isPositive) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Positivo",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    TaskType.TODO -> {
                        Checkbox(
                            checked = isTodoChecked,
                            onCheckedChange = null,
                            enabled = !isTodoChecked
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyIndicator(difficulty: Difficulty) {
    val (color, starCount) = when (difficulty) {
        Difficulty.TRIVIAL -> MaterialTheme.colorScheme.primary to 1
        Difficulty.EASY -> Color(0xFF8BC34A) to 2
        Difficulty.MEDIUM -> Color(0xFFFFC107) to 3
        Difficulty.HARD -> MaterialTheme.colorScheme.error to 4
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        repeat(starCount) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}