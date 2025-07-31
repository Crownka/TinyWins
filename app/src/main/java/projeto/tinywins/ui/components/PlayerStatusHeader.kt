package projeto.tinywins.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background // IMPORT ADICIONADO E CORRETO
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import projeto.tinywins.data.PlayerStats

@Composable
fun PlayerStatusHeader(
    playerStats: PlayerStats?,
    onClick: () -> Unit
) {
    val stats = playerStats ?: PlayerStats()
    val healthProgress = (stats.health.toFloat() / stats.maxHealth.toFloat()).coerceIn(0f, 1f)
    val xpToNextLevel = stats.xpToNextLevel()
    val xpProgress = if (xpToNextLevel > 0) (stats.xp.toFloat() / xpToNextLevel.toFloat()).coerceIn(0f, 1f) else 0f

    val animatedHealth by animateFloatAsState(targetValue = healthProgress, animationSpec = tween(500), label = "health")
    val animatedXp by animateFloatAsState(targetValue = xpProgress, animationSpec = tween(500), label = "xp")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (stats.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = stats.photoUrl,
                    contentDescription = "Avatar do Jogador",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar Padrão",
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }


            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = stats.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = "Nível ${stats.level}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Row {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Moedas", tint = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${stats.coins}", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.Diamond, contentDescription = "Diamantes", tint = Color(0xFF4FC3F7))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${stats.diamonds}", fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                StatusBar(label = "Saúde", progress = animatedHealth, progressColor = Color(0xFFE53935), progressText = "${stats.health}/${stats.maxHealth}")
                Spacer(modifier = Modifier.height(4.dp))
                StatusBar(label = "XP", progress = animatedXp, progressColor = Color(0xFFFFC107), progressText = "${stats.xp}/${xpToNextLevel}")
            }
        }
    }
}

@Composable
private fun StatusBar(label: String, progress: Float, progressColor: Color, progressText: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = progressText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp)),
        color = progressColor,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
    )
}