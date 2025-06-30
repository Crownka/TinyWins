package projeto.tinywins.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import projeto.tinywins.R
import projeto.tinywins.ui.theme.TinyWinsTheme

@Composable
fun PlayerStatusHeader(
    onClick: () -> Unit
) {
    val playerName = "Patrick"
    val playerLevel = 1
    val health = 45f / 50f
    val experience = 124f / 500f
    val playerCoins = 250
    val playerDiamonds = 12

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
            Image(
                painter = painterResource(id = R.drawable.pixel_avatar),
                contentDescription = "Avatar do Jogador",
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = playerName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = "Nível $playerLevel", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Row {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Moedas", tint = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$playerCoins", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.Diamond, contentDescription = "Diamantes", tint = Color(0xFF4FC3F7))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$playerDiamonds", fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                StatusBar(label = "Saúde", progress = health, progressColor = Color(0xFFE53935), progressText = "45/50")
                Spacer(modifier = Modifier.height(4.dp))
                StatusBar(label = "XP", progress = experience, progressColor = Color(0xFFFFC107), progressText = "124/500")
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
        modifier = Modifier.height(8.dp).fillMaxWidth().clip(RoundedCornerShape(4.dp)),
        color = progressColor,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
    )
}

@Preview
@Composable
private fun PlayerStatusHeaderPreview() {
    TinyWinsTheme(useDarkTheme = false) {
        PlayerStatusHeader(onClick = {})
    }
}