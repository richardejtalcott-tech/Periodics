package com.richardtalcott.periodic

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private data class ProjectedParticle(val position: Offset, val depth: Float, val proton: Boolean)

@Composable
internal fun InteractiveAtomModel(
    element: ElementProperties,
    electrons: Int = element.atomicNumber,
    neutrons: Int = element.representativeMassNumber - element.atomicNumber,
    modifier: Modifier = Modifier,
    cloudMode: Boolean = false,
    nucleusOnly: Boolean = false,
    running: Boolean = true,
    nucleusScale: Float = 0.58f,
    shellsOverride: List<Int>? = null,
) {
    var nucleusRotationX by remember(element.atomicNumber, neutrons) { mutableFloatStateOf(-12f) }
    var nucleusRotationY by remember(element.atomicNumber, neutrons) { mutableFloatStateOf(18f) }
    val orbit by rememberInfiniteTransition(label = "electron-motion").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9_500, easing = LinearEasing)),
        label = "electron-angle",
    )
    val modifierWithRotation = modifier.pointerInput(element.atomicNumber, neutrons) {
        detectDragGestures { change, dragAmount ->
            change.consume()
            nucleusRotationY = (nucleusRotationY + dragAmount.x * 0.42f) % 360f
            nucleusRotationX = (nucleusRotationX + dragAmount.y * 0.32f).coerceIn(-80f, 80f)
        }
    }
    Canvas(modifierWithRotation) {
        val center = Offset(size.width * 0.5f, size.height * 0.47f)
        val span = min(size.width, size.height)
        val nucleusRadius = span * 0.135f * nucleusScale
        val motion = if (running) orbit else 28f
        drawCircle(
            Brush.radialGradient(listOf(ElectricCyan.copy(alpha = 0.17f), Color.Transparent), center, span * 0.44f),
            span * 0.44f,
            center,
        )

        val totalNucleons = (element.atomicNumber + neutrons).coerceAtLeast(1)
        val visibleNucleons = min(totalNucleons, 128)
        val visibleProtons = (visibleNucleons * element.atomicNumber.toFloat() / totalNucleons).roundToInt()
        val rotationX = Math.toRadians(nucleusRotationX.toDouble())
        val rotationY = Math.toRadians(nucleusRotationY.toDouble())
        val projected = (0 until visibleNucleons).map { index ->
            val z0 = 1f - 2f * (index + 0.5f) / visibleNucleons
            val planar = sqrt((1f - z0 * z0).coerceAtLeast(0f))
            val theta = index * 2.399963f
            val x0 = planar * cos(theta)
            val y0 = planar * sin(theta)
            val x1 = x0 * cos(rotationY).toFloat() + z0 * sin(rotationY).toFloat()
            val z1 = -x0 * sin(rotationY).toFloat() + z0 * cos(rotationY).toFloat()
            val y2 = y0 * cos(rotationX).toFloat() - z1 * sin(rotationX).toFloat()
            val z2 = y0 * sin(rotationX).toFloat() + z1 * cos(rotationX).toFloat()
            val position = Offset(center.x + x1 * nucleusRadius, center.y + y2 * nucleusRadius * 0.82f)
            val proton = ((index * 37) % visibleNucleons) < visibleProtons
            ProjectedParticle(position, z2, proton)
        }.sortedBy { it.depth }
        val particleRadius = (nucleusRadius * 0.34f / sqrt(visibleNucleons / 10f)).coerceIn(2.2f, nucleusRadius * 0.27f)
        projected.forEach { particle ->
            val tint = if (particle.proton) ProtonRed else NeutronBlue
            val scale = 0.82f + (particle.depth + 1f) * 0.09f
            val radius = particleRadius * scale
            drawCircle(tint.copy(alpha = 0.24f), radius * 1.55f, particle.position)
            drawCircle(
                Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.95f), tint, tint.copy(alpha = 0.56f)),
                    Offset(particle.position.x - radius * 0.28f, particle.position.y - radius * 0.32f),
                    radius * 1.35f,
                ),
                radius,
                particle.position,
            )
        }

        if (!nucleusOnly && electrons > 0) {
            val shells = shellsOverride ?: ionShellPopulation(element, electrons)
            val tilts = listOf(-62f, -28f, 18f, 52f, 76f, -44f, 33f)
            shells.forEachIndexed { shellIndex, count ->
                if (count <= 0) return@forEachIndexed
                val rx = span * (0.20f + shellIndex * 0.052f)
                val ry = rx * (0.42f + (shellIndex % 2) * 0.09f)
                val tilt = tilts[shellIndex % tilts.size]
                if (!cloudMode) {
                    rotate(tilt, center) {
                        drawOval(
                            ElectricCyan.copy(alpha = 0.46f),
                            topLeft = Offset(center.x - rx, center.y - ry),
                            size = Size(rx * 2f, ry * 2f),
                            style = Stroke((1.5f + shellIndex * 0.15f).coerceAtMost(2.5f)),
                        )
                    }
                    repeat(count) { electronIndex ->
                        val direction = if (shellIndex % 2 == 0) 1f else -1f
                        val angle = Math.toRadians((motion * direction * (1f + shellIndex * 0.035f) + electronIndex * 360f / count).toDouble())
                        val localX = cos(angle).toFloat() * rx
                        val localY = sin(angle).toFloat() * ry
                        val radians = Math.toRadians(tilt.toDouble())
                        val point = Offset(
                            center.x + localX * cos(radians).toFloat() - localY * sin(radians).toFloat(),
                            center.y + localX * sin(radians).toFloat() + localY * cos(radians).toFloat(),
                        )
                        val electronRadius = (span * 0.012f).coerceAtLeast(3f)
                        drawCircle(ElectricCyan.copy(alpha = 0.2f), electronRadius * 2.2f, point)
                        drawCircle(
                            Brush.radialGradient(listOf(Color.White, ElectricCyan, LaboratoryBlue), point, electronRadius * 1.4f),
                            electronRadius,
                            point,
                        )
                    }
                } else {
                    repeat(130) { index ->
                        val angle = index * 2.399963f + shellIndex * 0.71f
                        val radial = 0.72f + ((index * 31) % 29) / 100f
                        val point = Offset(center.x + cos(angle) * rx * radial, center.y + sin(angle) * ry * radial)
                        drawCircle(ElectricCyan.copy(alpha = 0.035f + (index % 5) * 0.012f), 1.1f + index % 3, point)
                    }
                }
            }
        }
    }
}

@Composable
internal fun ElementSpecimen(
    element: ElementProperties,
    modifier: Modifier = Modifier,
    accent: Color = ElectricCyan,
) {
    Canvas(modifier) {
        val center = Offset(size.width / 2f, size.height * 0.48f)
        val radius = min(size.width, size.height) * 0.31f
        drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.22f), Color.Transparent), center, radius * 1.7f), radius * 1.7f, center)
        val points = 13
        val path = Path()
        repeat(points) { index ->
            val angle = index * 2f * PI.toFloat() / points
            val variation = 0.72f + (((index * 47 + element.atomicNumber * 17) % 31) / 100f)
            val x = center.x + cos(angle) * radius * variation
            val y = center.y + sin(angle) * radius * variation * 0.72f
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        val base = element.visualColor()
        drawPath(path, base.copy(alpha = 0.24f), style = Stroke(radius * 0.16f))
        drawPath(
            path,
            Brush.linearGradient(
                listOf(Color.White.copy(alpha = 0.82f), base, base.copy(alpha = 0.36f), Color.Black),
                Offset(center.x - radius, center.y - radius),
                Offset(center.x + radius, center.y + radius),
            ),
        )
        repeat(9) { index ->
            val y = center.y - radius * 0.45f + index * radius * 0.11f
            drawLine(Color.White.copy(alpha = 0.08f + index % 3 * 0.025f), Offset(center.x - radius * 0.55f, y), Offset(center.x + radius * 0.42f, y + radius * 0.18f), 1.2f)
        }
        val platformY = size.height * 0.82f
        drawOval(Color.Black.copy(alpha = 0.86f), Offset(size.width * 0.13f, platformY - size.height * 0.07f), Size(size.width * 0.74f, size.height * 0.14f))
        repeat(3) { index ->
            drawOval(accent.copy(alpha = 0.45f - index * 0.1f), Offset(size.width * (0.16f + index * 0.025f), platformY - size.height * (0.055f - index * 0.005f)), Size(size.width * (0.68f - index * 0.05f), size.height * (0.11f - index * 0.01f)), style = Stroke(2f))
        }
    }
}

@Composable
internal fun MoleculeVisual(
    species: BondSpecies,
    modifier: Modifier = Modifier,
    showElectronDensity: Boolean = true,
    resetKey: Int = 0,
) {
    var rotation by remember(species.formula, resetKey) { mutableFloatStateOf(0f) }
    var tilt by remember(species.formula, resetKey) { mutableFloatStateOf(-12f) }
    val drift by rememberInfiniteTransition(label = "molecule").animateFloat(
        0f,
        360f,
        infiniteRepeatable(tween(12_000, easing = LinearEasing), RepeatMode.Restart),
        label = "molecule-drift",
    )
    val parsedAtoms = remember(species.formula) { parseFormulaAtoms(species.formula) }
    // Formula order is not always display order: H2O is written hydrogen-first,
    // but oxygen is the central atom.  Keep the rendered geometry scientifically meaningful.
    val atoms = remember(species.formula) {
        when (species.formula) {
            "H2O" -> listOf(elementByAtomicNumber(8), elementByAtomicNumber(1), elementByAtomicNumber(1))
            "CO2" -> listOf(elementByAtomicNumber(6), elementByAtomicNumber(8), elementByAtomicNumber(8))
            "NH3" -> listOf(elementByAtomicNumber(7)) + List(3) { elementByAtomicNumber(1) }
            "CH4" -> listOf(elementByAtomicNumber(6)) + List(4) { elementByAtomicNumber(1) }
            else -> parsedAtoms
        }
    }
    Canvas(
        modifier.pointerInput(species.formula) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                rotation = (rotation + dragAmount.x * 0.5f) % 360f
                tilt = (tilt + dragAmount.y * 0.32f).coerceIn(-68f, 68f)
            }
        },
    ) {
        val center = Offset(size.width / 2f, size.height * 0.43f)
        val span = min(size.width, size.height)
        drawCircle(Brush.radialGradient(listOf(ElectricCyan.copy(alpha = 0.15f), Color.Transparent), center, span * 0.48f), span * 0.48f, center)
        val ionic = species.bondType.contains("ionic", ignoreCase = true)
        val basePositions: List<Offset> = when {
            ionic -> atoms.indices.map { index ->
                val columns = 3
                val column = index % columns
                val row = index / columns
                Offset(
                    center.x + (column - 1) * span * 0.16f + (row % 2) * span * 0.075f,
                    center.y + (row - 0.5f) * span * 0.15f,
                )
            }
            atoms.size == 1 -> listOf(center)
            atoms.size == 2 -> listOf(
                Offset(center.x - span * 0.14f, center.y),
                Offset(center.x + span * 0.14f, center.y),
            )
            species.formula == "H2O" -> listOf(
                center,
                Offset(center.x - span * 0.22f, center.y + span * 0.17f),
                Offset(center.x + span * 0.22f, center.y + span * 0.17f),
            )
            species.formula == "CO2" -> listOf(
                center,
                Offset(center.x - span * 0.25f, center.y),
                Offset(center.x + span * 0.25f, center.y),
            )
            species.formula == "NH3" -> listOf(
                center,
                Offset(center.x, center.y - span * 0.23f),
                Offset(center.x - span * 0.22f, center.y + span * 0.16f),
                Offset(center.x + span * 0.22f, center.y + span * 0.16f),
            )
            species.formula == "CH4" -> listOf(
                center,
                Offset(center.x, center.y - span * 0.24f),
                Offset(center.x - span * 0.23f, center.y + span * 0.07f),
                Offset(center.x + span * 0.23f, center.y + span * 0.07f),
                Offset(center.x, center.y + span * 0.24f),
            )
            else -> atoms.indices.map { index ->
                if (index == 0) center else {
                    val angle = index * 2f * PI.toFloat() / maxOf(2, atoms.size - 1)
                    Offset(center.x + cos(angle) * span * 0.21f, center.y + sin(angle) * span * 0.15f)
                }
            }
        }
        val rotationRadians = Math.toRadians((rotation + drift * 0.025f).toDouble())
        val tiltRadians = Math.toRadians(tilt.toDouble())
        val positions = basePositions.map { point ->
            val x = point.x - center.x
            val y = point.y - center.y
            val rotatedX = x * cos(rotationRadians).toFloat() - y * sin(rotationRadians).toFloat()
            val rotatedY = x * sin(rotationRadians).toFloat() + y * cos(rotationRadians).toFloat()
            Offset(center.x + rotatedX, center.y + rotatedY * cos(tiltRadians).toFloat())
        }
        val bonds = when {
            ionic || positions.size < 2 -> emptyList()
            positions.size == 2 -> listOf(0 to 1)
            else -> positions.indices.drop(1).map { 0 to it }
        }
        bonds.forEach { (from, to) ->
            drawLine(Color.White.copy(alpha = 0.72f), positions[from], positions[to], span * 0.035f)
            drawLine(ElectricCyan.copy(alpha = 0.45f), positions[from], positions[to], span * 0.012f)
        }
        atoms.zip(positions).forEach { (atom, point) ->
            val atomRadius = span * if (atom.atomicNumber == 1) 0.075f else 0.105f
            val color = atom.visualColor()
            if (showElectronDensity) drawCircle(color.copy(alpha = 0.22f), atomRadius * 1.72f, point)
            drawCircle(
                Brush.radialGradient(listOf(Color.White, color, color.copy(alpha = 0.58f)), Offset(point.x - atomRadius * 0.28f, point.y - atomRadius * 0.32f), atomRadius * 1.35f),
                atomRadius,
                point,
            )
        }
        if (showElectronDensity && species.formula == "H2O") {
            val oxygen = positions.first()
            listOf(-1f, 1f).forEach { side ->
                drawOval(
                    Brush.radialGradient(listOf(ElectricCyan.copy(alpha = 0.34f), Color.Transparent)),
                    Offset(oxygen.x + side * span * 0.09f - span * 0.07f, oxygen.y - span * 0.25f),
                    Size(span * 0.14f, span * 0.22f),
                )
            }
        }
    }
}

@Composable
internal fun MatterParticles(
    element: ElementProperties,
    phase: MatterPhase,
    modifier: Modifier = Modifier,
    running: Boolean = true,
) {
    val motion by rememberInfiniteTransition(label = "matter-motion").animateFloat(
        0f,
        1f,
        infiniteRepeatable(tween(2_800, easing = LinearEasing), RepeatMode.Restart),
        label = "matter-phase",
    )
    Canvas(modifier) {
        val frame = if (running) motion else 0.35f
        val tint = when (phase) {
            MatterPhase.LIQUID -> WarmAmber
            MatterPhase.GAS -> ElectricCyan
            MatterPhase.PLASMA -> Color(0xFFC46BFF)
            MatterPhase.SOLID -> element.visualColor()
            MatterPhase.UNKNOWN -> Color.Gray
        }
        val chamber = androidx.compose.ui.geometry.Rect(size.width * 0.08f, size.height * 0.12f, size.width * 0.92f, size.height * 0.86f)
        drawRoundRect(Color.Black.copy(alpha = 0.38f), chamber.topLeft, chamber.size, cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f))
        drawRoundRect(ElectricCyan.copy(alpha = 0.35f), chamber.topLeft, chamber.size, cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f), style = Stroke(2f))
        val count = 38
        repeat(count) { index ->
            val point = when (phase) {
                MatterPhase.SOLID -> {
                    val columns = 8
                    val row = index / columns
                    val column = index % columns
                    Offset(chamber.left + chamber.width * (column + 1) / 9f, chamber.top + chamber.height * (row + 1) / 6f + sin(frame * 2f * PI.toFloat() + index) * 1.8f)
                }
                MatterPhase.LIQUID -> {
                    val x = chamber.left + chamber.width * (0.08f + ((index * 47) % 83) / 100f)
                    val baseY = chamber.top + chamber.height * (0.48f + ((index * 31) % 44) / 100f)
                    Offset(x + sin(frame * 2f * PI.toFloat() + index) * 7f, baseY + cos(frame * 2f * PI.toFloat() + index * 0.6f) * 5f)
                }
                MatterPhase.GAS, MatterPhase.PLASMA -> {
                    val x = chamber.left + (chamber.width * (((index * 73) % 97) / 97f) + frame * chamber.width * (0.18f + index % 4 * 0.05f)) % chamber.width
                    val y = chamber.top + (chamber.height * (((index * 41) % 89) / 89f) + frame * chamber.height * (0.14f + index % 3 * 0.07f)) % chamber.height
                    Offset(x, y)
                }
                MatterPhase.UNKNOWN -> Offset(chamber.center.x, chamber.center.y)
            }
            val radius = if (phase == MatterPhase.PLASMA) 4.2f else 5.2f
            drawCircle(tint.copy(alpha = 0.22f), radius * 2.1f, point)
            drawCircle(Brush.radialGradient(listOf(Color.White, tint, tint.copy(alpha = 0.45f)), point, radius * 1.4f), radius, point)
        }
        if (phase == MatterPhase.LIQUID) {
            val surfaceY = chamber.top + chamber.height * 0.52f
            drawLine(WarmAmber.copy(alpha = 0.7f), Offset(chamber.left + 8f, surfaceY), Offset(chamber.right - 8f, surfaceY), 3f)
        }
    }
}
