package projeto.tinywins.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import projeto.tinywins.data.ChallengeCategory
import projeto.tinywins.data.toColor
import projeto.tinywins.data.toIcon
import projeto.tinywins.ui.theme.TinyWinsTheme

@Composable
fun CategoryBanner(
    category: ChallengeCategory,
    modifier: Modifier = Modifier
) {
    Surface(
        color = category.toColor().copy(alpha = 0.1f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = category.toIcon,
                contentDescription = "Categoria ${category.name}",
                modifier = Modifier.size(48.dp),
                tint = category.toColor()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name.lowercase().replaceFirstChar { it.titlecase() }.replace("_", " "),
                style = MaterialTheme.typography.titleLarge,
                color = category.toColor(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
private fun CategoryBannerPreview() {
    TinyWinsTheme(useDarkTheme = false) {
        CategoryBanner(category = ChallengeCategory.PRODUTIVIDADE)
    }
}