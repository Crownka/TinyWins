package projeto.tinywins.ui.components // Pacote corrigido

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import projeto.tinywins.data.ChallengeCategory
import projeto.tinywins.data.TinyWinChallenge
import projeto.tinywins.data.sampleChallenges
import projeto.tinywins.data.toColor
import projeto.tinywins.data.toIcon
import projeto.tinywins.ui.theme.TinyWinsTheme

@Composable
fun ChallengeItemCard(
    challenge: TinyWinChallenge,
    onClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(challenge.category.toColor())
            )

            Icon(
                imageVector = challenge.category.toIcon,
                contentDescription = "Categoria ${challenge.category.name}",
                modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                tint = challenge.category.toColor()
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${challenge.xp} XP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = onCompleteClick,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Completar Desafio",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChallengeItemCardPreview() {
    val challenge = sampleChallenges.first { it.category == ChallengeCategory.PRODUTIVIDADE }
    TinyWinsTheme(useDarkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            ChallengeItemCard(
                challenge = challenge,
                onClick = {},
                onCompleteClick = {}
            )
        }
    }
}