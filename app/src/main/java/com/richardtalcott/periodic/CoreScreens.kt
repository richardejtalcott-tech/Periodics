package com.richardtalcott.periodic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
internal fun PeriodicTableScreen(onElementSelected: (ElementProperties) -> Unit) {
    var zoom by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var twist by remember { mutableFloatStateOf(0f) }
    val transformState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        zoom = (zoom * zoomChange).coerceIn(0.82f, 2.15f)
        pan = Offset(
            (pan.x + panChange.x).coerceIn(-520f, 520f),
            (pan.y + panChange.y).coerceIn(-320f, 320f),
        )
        twist = (twist + rotationChange).coerceIn(-10f, 10f)
    }
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().height(62.dp).padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("PERIODIC", fontSize = 27.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                Text("THE INTERACTIVE ELEMENT LABORATORY", fontSize = 9.sp, color = ElectricCyan)
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("118 ELEMENTS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("TAP A 3D ELEMENT TILE TO EXPLORE", fontSize = 8.sp, color = ElectricCyan)
            }
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 22.dp, vertical = 4.dp)) {
            val tableHeight = maxHeight - 30.dp
            val cellWidth = maxWidth / 18
            val cellHeight = tableHeight / 9
            Canvas(Modifier.fillMaxSize()) {
                val horizon = size.height * 0.73f
                drawOval(
                    Brush.radialGradient(listOf(ElectricCyan.copy(alpha = 0.18f), Color.Transparent)),
                    Offset(size.width * 0.08f, horizon - size.height * 0.13f),
                    Size(size.width * 0.84f, size.height * 0.27f),
                )
                repeat(5) { index ->
                    drawOval(
                        ElectricCyan.copy(alpha = 0.24f - index * 0.035f),
                        Offset(size.width * (0.09f + index * 0.018f), horizon - size.height * (0.085f - index * 0.006f)),
                        Size(size.width * (0.82f - index * 0.036f), size.height * (0.17f - index * 0.012f)),
                        style = Stroke(1.7f),
                    )
                }
            }
            Box(
                Modifier.fillMaxWidth().height(tableHeight).align(Alignment.TopCenter)
                    .graphicsLayer {
                        scaleX = zoom
                        scaleY = zoom
                        translationX = pan.x
                        translationY = pan.y
                        rotationX = (-pan.y / 62f).coerceIn(-8f, 8f)
                        rotationY = (pan.x / 72f).coerceIn(-9f, 9f)
                        rotationZ = twist
                        cameraDistance = 22f * density
                        shadowElevation = 22f
                    }
                    .transformable(transformState),
            ) {
                ELEMENTS.forEach { element ->
                    val position = tablePosition(element.atomicNumber)
                    ElementTile(
                        element,
                        Modifier.offset(cellWidth * (position.column - 1), cellHeight * (position.row - 1))
                            .size(cellWidth, cellHeight)
                            .padding(horizontal = 1.dp, vertical = 1.dp)
                            .clickable { onElementSelected(element) },
                    )
                }
            }
            Row(
                Modifier.align(Alignment.BottomCenter)
                    .background(Color(0xF0061724), RoundedCornerShape(20.dp))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 13.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("DRAG • MOVE + TILT", fontSize = 7.sp, color = ElectricCyan)
                Text("PINCH • ZOOM", fontSize = 7.sp, color = ElectricCyan)
                Text("TWIST • ROTATE", fontSize = 7.sp, color = ElectricCyan)
                Text("${(zoom * 100).roundToInt()}%", fontSize = 7.sp, color = WarmAmber)
                Text(
                    "RESET",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        zoom = 1f
                        pan = Offset.Zero
                        twist = 0f
                    },
                )
            }
        }
    }
}

@Composable
private fun ElementTile(element: ElementProperties, modifier: Modifier = Modifier) {
    val accent = categoryColor(element.category)
    val shape = RoundedCornerShape(6.dp)
    Box(modifier) {
        Box(
            Modifier.fillMaxSize().padding(start = 5.dp, top = 6.dp)
                .background(Color.Black.copy(alpha = 0.88f), shape)
                .border(1.dp, accent.copy(alpha = 0.38f), shape),
        )
        Box(
            Modifier.fillMaxSize().padding(start = 1.dp, top = 1.dp, end = 4.dp, bottom = 6.dp)
                .background(
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), accent, accent.copy(alpha = 0.76f), Color(0xFF020B13))),
                    shape,
                )
                .border(1.dp, Color.White.copy(alpha = 0.58f), shape),
        ) {
            Text(
                element.atomicNumber.toString(),
                fontSize = 5.5.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.align(Alignment.TopStart).padding(start = 3.dp, top = 1.dp),
            )
            Text(
                element.symbol,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center).offset(y = (-2).dp),
            )
            Box(
                Modifier.fillMaxWidth().height(8.dp).align(Alignment.BottomCenter)
                    .background(Color(0xC9020A12), RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    element.name.uppercase(),
                    fontSize = 3.7.sp,
                    color = Color.White.copy(alpha = 0.94f),
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                Modifier.fillMaxWidth().height(1.dp).align(Alignment.TopCenter)
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.72f), Color.Transparent))),
            )
        }
    }
}

@Composable
internal fun ElementInformationScreen(
    element: ElementProperties,
    onBack: () -> Unit,
    onNavigate: (AppPage) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            "${element.name.uppercase()} (${element.symbol})",
            "ATOMIC NUMBER ${element.atomicNumber} • ${element.category.uppercase()}",
            onBack,
        )
        Row(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GlassPanel(Modifier.weight(0.72f).fillMaxHeight()) {
                Column(Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(element.atomicNumber.toString(), fontSize = 17.sp, color = ElectricCyan)
                        Text(element.category.uppercase(), fontSize = 9.sp, color = WarmAmber)
                    }
                    Text(element.symbol, fontSize = 54.sp, fontWeight = FontWeight.Black)
                    Text(element.name, fontSize = 19.sp)
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        ElementSpecimen(element, Modifier.fillMaxSize())
                    }
                    Text(
                        conciseElementDescription(element),
                        fontSize = 8.sp,
                        lineHeight = 11.sp,
                        color = Color.White.copy(alpha = 0.78f),
                    )
                }
            }
            GlassPanel(Modifier.weight(1.48f).fillMaxHeight()) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        ExhibitPlatform(Modifier.fillMaxSize())
                        InteractiveAtomModel(element, modifier = Modifier.fillMaxSize(), nucleusScale = 0.48f)
                        Text(
                            "INTERACTIVE ATOM MODEL",
                            fontSize = 10.sp,
                            color = ElectricCyan,
                            modifier = Modifier.align(Alignment.TopCenter),
                        )
                        Text(
                            "Drag the model to rotate the nucleus • illustrative, not to scale",
                            fontSize = 7.sp,
                            color = Color.White.copy(alpha = 0.62f),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp),
                        )
                    }
                    Column(Modifier.width(190.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                        PanelTitle("ELEMENT PROPERTIES")
                        MetricRow("Atomic mass", "${element.atomicMass} u")
                        MetricRow("Density", established(element.densityGPerCm3, "g/cm³"))
                        MetricRow("Melting point", temperatureLabel(element.meltingPointK))
                        MetricRow("Boiling point", temperatureLabel(element.boilingPointK))
                        MetricRow("Electronegativity", established(element.electronegativity))
                        MetricRow("Configuration", element.electronConfiguration)
                        MetricRow("Oxidation states", established(element.oxidationStates), valueColor = WarmAmber)
                        MetricRow("Standard state", established(element.standardState))
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 22.dp, vertical = 4.dp)
                .background(Color(0xF0061724), RoundedCornerShape(22.dp))
                .border(1.dp, ElectricCyan.copy(alpha = 0.42f), RoundedCornerShape(22.dp))
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(
                "ATOM" to AppPage.ATOM,
                "ISOTOPES" to AppPage.ISOTOPE,
                "IONS" to AppPage.ION,
                "BONDS" to AppPage.BOND,
                "MATTER" to AppPage.STATES,
                "COMPARE" to AppPage.COMPARE,
            ).forEach { (label, page) ->
                ScienceButton(label, { onNavigate(page) }, Modifier.weight(1f))
            }
        }
    }
}

private fun temperatureLabel(kelvin: String): String {
    val value = kelvin.toDoubleOrNull() ?: return "Not established"
    return "${formatFahrenheit(kelvinToFahrenheit(value))} • ${value.roundToInt()} K"
}

@Composable
internal fun AtomExplorerScreen(element: ElementProperties, onBack: () -> Unit) {
    var mode by remember(element.atomicNumber) { mutableStateOf("SHELLS") }
    var running by remember(element.atomicNumber) { mutableStateOf(true) }
    val neutrons = element.representativeMassNumber - element.atomicNumber
    val shells = neutralShellPopulation(element)
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            "${element.name.uppercase()} (${element.symbol})",
            "ATOM EXPLORER • INTERACTIVE STRUCTURE AND ENERGY LEVELS",
            onBack,
        )
        Row(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            GlassPanel(Modifier.width(190.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    Text(element.symbol, fontSize = 52.sp, fontWeight = FontWeight.Black)
                    MetricRow("Atomic number", element.atomicNumber.toString())
                    MetricRow("Protons", element.atomicNumber.toString(), valueColor = ProtonRed)
                    MetricRow("Neutrons", neutrons.toString(), valueColor = NeutronBlue)
                    MetricRow("Electrons", element.atomicNumber.toString())
                    MetricRow("Mass number", element.representativeMassNumber.toString(), valueColor = WarmAmber)
                    HorizontalDivider(color = ElectricCyan.copy(alpha = 0.22f))
                    Text("SHELL POPULATION", fontSize = 8.sp, color = ElectricCyan)
                    Text(shells.joinToString(" • "), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            GlassPanel(Modifier.weight(1f).fillMaxHeight()) {
                Box(Modifier.fillMaxSize()) {
                    ExhibitPlatform(Modifier.fillMaxSize())
                    InteractiveAtomModel(
                        element = element,
                        modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
                        cloudMode = mode == "CLOUD",
                        nucleusOnly = mode == "NUCLEUS",
                        running = running,
                        nucleusScale = 0.42f,
                    )
                    PanelTitle(
                        when (mode) {
                            "CLOUD" -> "ELECTRON PROBABILITY CLOUD"
                            "NUCLEUS" -> "ROTATABLE NUCLEUS"
                            else -> "OVAL 3D SHELL MODEL"
                        },
                        color = ElectricCyan,
                    )
                    Row(
                        Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        listOf("SHELLS", "CLOUD", "NUCLEUS").forEach { option ->
                            ScienceButton(option, { mode = option }, Modifier.weight(1f), selected = mode == option)
                        }
                        ScienceButton(if (running) "PAUSE" else "RESUME", { running = !running }, Modifier.weight(1f))
                    }
                }
            }
            GlassPanel(Modifier.width(210.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    PanelTitle("SCIENTIFIC MODEL")
                    Text(
                        "Nucleus and particles are enlarged. Oval shell paths depict energy levels and are not literal planetary orbits. Cloud mode better represents electron probability.",
                        fontSize = 9.sp,
                        lineHeight = 13.sp,
                        color = Color.White.copy(alpha = 0.82f),
                    )
                    HorizontalDivider(color = ElectricCyan.copy(alpha = 0.22f))
                    MetricRow("Configuration", element.electronConfiguration)
                    MetricRow("Neutral charge", "0")
                    MetricRow("Model scale", "Illustrative", valueColor = WarmAmber)
                    Text("DRAG THE ATOM TO ROTATE THE NUCLEUS", fontSize = 7.sp, color = ElectricCyan)
                }
            }
        }
    }
}
