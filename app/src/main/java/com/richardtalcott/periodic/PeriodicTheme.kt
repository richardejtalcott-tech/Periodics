package com.richardtalcott.periodic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val DeepInk = Color(0xFF01050B)
internal val Navy = Color(0xFF061522)
internal val PanelTop = Color(0xF00B263A)
internal val PanelBottom = Color(0xF505101A)
internal val ElectricCyan = Color(0xFF45D5FF)
internal val LaboratoryBlue = Color(0xFF208DFF)
internal val WarmAmber = Color(0xFFFFB84D)
internal val ProtonRed = Color(0xFFFF496F)
internal val NeutronBlue = Color(0xFF548EFF)
internal val PositiveGreen = Color(0xFF65E08A)

internal enum class AppPage {
    TABLE,
    INFO,
    ATOM,
    ISOTOPE,
    ION,
    BOND,
    STATES,
    COMPARE,
}

@Composable
internal fun PeriodicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = ElectricCyan,
            secondary = WarmAmber,
            background = DeepInk,
            surface = Navy,
            onBackground = Color.White,
            onSurface = Color.White,
        ),
        content = content,
    )
}

@Composable
internal fun LaboratoryOverlay() {
    Canvas(Modifier.fillMaxSize()) {
        drawRect(
            Brush.radialGradient(
                listOf(Color.Transparent, DeepInk.copy(alpha = 0.12f), DeepInk.copy(alpha = 0.72f)),
                center = Offset(size.width * 0.5f, size.height * 0.48f),
                radius = size.width * 0.78f,
            ),
        )
        repeat(70) { index ->
            val x = ((index * 149) % 997) / 997f * size.width
            val y = ((index * 83) % 673) / 673f * size.height * 0.82f
            drawCircle(Color.White.copy(alpha = 0.05f + (index % 5) * 0.018f), 0.7f + index % 3, Offset(x, y))
        }
        val horizon = size.height * 0.78f
        repeat(13) { index ->
            val x = size.width * index / 12f
            drawLine(ElectricCyan.copy(alpha = 0.055f), Offset(size.width / 2f, horizon), Offset(x, size.height), 1.2f)
        }
        repeat(4) { index ->
            val y = horizon + index * size.height * 0.055f
            drawLine(ElectricCyan.copy(alpha = 0.05f), Offset(0f, y), Offset(size.width, y), 1f)
        }
    }
}

@Composable
internal fun ScreenHeader(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            Box(
                Modifier.size(46.dp)
                    .background(Color(0xE3081A29), CircleShape)
                    .border(1.dp, ElectricCyan.copy(alpha = 0.5f), CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text("‹", fontSize = 32.sp, fontWeight = FontWeight.Light, color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
        }
        Column {
            Text(
                title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                subtitle,
                fontSize = 10.sp,
                color = ElectricCyan,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun GlassPanel(
    modifier: Modifier = Modifier,
    accent: Color = ElectricCyan,
    padding: Dp = 14.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier
            .background(
                Brush.verticalGradient(listOf(PanelTop, PanelBottom)),
                RoundedCornerShape(17.dp),
            )
            .border(1.dp, accent.copy(alpha = 0.48f), RoundedCornerShape(17.dp))
            .padding(padding),
        content = content,
    )
}

@Composable
internal fun PanelTitle(text: String, color: Color = ElectricCyan) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
}

@Composable
internal fun MetricRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = ElectricCyan,
    compact: Boolean = false,
) {
    Row(
        modifier.fillMaxWidth().padding(vertical = if (compact) 2.dp else 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label.uppercase(), fontSize = if (compact) 7.sp else 8.sp, color = Color.White.copy(alpha = 0.58f))
        Spacer(Modifier.width(8.dp))
        Text(
            value,
            fontSize = if (compact) 8.sp else 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun ScienceButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    accent: Color = ElectricCyan,
    contentPadding: PaddingValues = PaddingValues(horizontal = 11.dp, vertical = 7.dp),
) {
    if (selected) {
        Button(
            onClick,
            modifier,
            colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = DeepInk),
            contentPadding = contentPadding,
        ) {
            Text(text, fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    } else {
        OutlinedButton(
            onClick,
            modifier,
            border = BorderStroke(1.dp, accent.copy(alpha = 0.62f)),
            contentPadding = contentPadding,
        ) {
            Text(text, fontSize = 8.sp, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
        }
    }
}

internal fun categoryColor(category: String): Color = when {
    category.contains("Alkali metal", ignoreCase = true) -> Color(0xFF26A88B)
    category.contains("Alkaline", ignoreCase = true) -> Color(0xFF2FB79D)
    category.contains("Transition", ignoreCase = true) -> Color(0xFF1C9BC9)
    category.contains("Post-transition", ignoreCase = true) -> Color(0xFF3BAF88)
    category.contains("Metalloid", ignoreCase = true) -> Color(0xFF6BA64A)
    category.equals("Nonmetal", ignoreCase = true) -> Color(0xFF36A783)
    category.contains("Halogen", ignoreCase = true) -> Color(0xFFA746A9)
    category.contains("Noble", ignoreCase = true) -> Color(0xFF3975C7)
    category.contains("Lanthanide", ignoreCase = true) -> Color(0xFF8056C7)
    category.contains("Actinide", ignoreCase = true) -> Color(0xFFC04A83)
    else -> Color(0xFF4F7794)
}

internal fun ElementProperties.visualColor(): Color {
    val parsed = cpkHexColor.takeIf { it.length == 6 }?.toLongOrNull(16)
    return parsed?.let { Color(0xFF000000 or it) } ?: categoryColor(category)
}

@Composable
internal fun ExhibitPlatform(modifier: Modifier = Modifier, accent: Color = ElectricCyan) {
    Canvas(modifier) {
        val center = Offset(size.width / 2f, size.height * 0.72f)
        val width = size.width * 0.72f
        drawOval(Color.Black.copy(alpha = 0.82f), Offset(center.x - width / 2f, center.y - size.height * 0.1f), androidx.compose.ui.geometry.Size(width, size.height * 0.2f))
        repeat(4) { index ->
            val shrink = index * size.width * 0.04f
            drawOval(
                accent.copy(alpha = 0.34f - index * 0.055f),
                Offset(center.x - width / 2f + shrink, center.y - size.height * 0.08f + index * 3f),
                androidx.compose.ui.geometry.Size(width - shrink * 2, size.height * 0.15f - index * 3f),
                style = Stroke(2.2f - index * 0.3f),
            )
        }
        val beam = Path().apply {
            moveTo(center.x - width * 0.24f, center.y - size.height * 0.06f)
            lineTo(center.x - width * 0.1f, 0f)
            lineTo(center.x + width * 0.1f, 0f)
            lineTo(center.x + width * 0.24f, center.y - size.height * 0.06f)
            close()
        }
        drawPath(beam, Brush.verticalGradient(listOf(accent.copy(alpha = 0.05f), accent.copy(alpha = 0.16f))))
    }
}
