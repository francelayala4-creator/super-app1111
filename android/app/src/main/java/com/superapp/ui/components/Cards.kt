package com.superapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.superapp.ui.theme.Champagne
import com.superapp.ui.theme.Purple500
import com.superapp.ui.theme.Purple700

@Composable
fun PremiumCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 10.dp,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(Modifier.padding(20.dp), content = content)
    }
}

@Composable
fun GradientHeroCard(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Purple700, Purple500, Champagne),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 12.dp,
    ) {
        Column(
            Modifier
                .background(Brush.linearGradient(colors))
                .padding(24.dp),
            content = content,
        )
    }
}

@Composable
fun ColorBlockCard(
    accent: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 8.dp,
    ) {
        Row {
            Box(
                Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(Modifier.padding(20.dp).weight(1f), content = content)
        }
    }
}

@Composable
fun Pill(
    text: String,
    bg: Color = MaterialTheme.colorScheme.primaryContainer,
    fg: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = fg)
    }
}

@Composable
fun KeyValueRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
