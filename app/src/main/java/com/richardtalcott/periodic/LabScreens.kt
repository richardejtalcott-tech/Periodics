package com.richardtalcott.periodic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
internal fun IsotopeLabScreen(element: ElementProperties, onBack: () -> Unit) {
    val nuclides = remember(element.atomicNumber) { nuclidesFor(element) }
    val defaultNuclide = nuclides.maxByOrNull { it.naturalAbundancePercent ?: -1.0 } ?: nuclides.first()
    var massNumber by remember(element.atomicNumber) { mutableIntStateOf(defaultNuclide.massNumber) }
    val lower = nuclides.minOf { it.massNumber }
    val upper = nuclides.maxOf { it.massNumber }
    val neutrons = massNumber - element.atomicNumber
    val profile = nuclides.firstOrNull { it.massNumber == massNumber }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("ISOTOPE LAB", "${element.name.uppercase()} • BUILD AND COMPARE ISOTOPES", onBack)
        Row(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 13.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GlassPanel(Modifier.width(205.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    PanelTitle("ISOTOPE CONTROLS")
                    Text("${element.symbol}-$massNumber", fontSize = 39.sp, color = WarmAmber)
                    MetricRow("Protons", element.atomicNumber.toString(), valueColor = ProtonRed)
                    MetricRow("Neutrons", neutrons.toString(), valueColor = NeutronBlue)
                    MetricRow("Mass number", massNumber.toString())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ScienceButton("−", { massNumber = (massNumber - 1).coerceAtLeast(lower) })
                        Text("ADJUST NEUTRONS", fontSize = 8.sp, color = Color.White.copy(alpha = 0.65f), modifier = Modifier.align(Alignment.CenterVertically))
                        ScienceButton("+", { massNumber = (massNumber + 1).coerceAtMost(upper) })
                    }
                    Slider(
                        value = massNumber.toFloat(),
                        onValueChange = { massNumber = it.roundToInt().coerceIn(lower, upper) },
                        valueRange = lower.toFloat()..upper.toFloat(),
                        steps = (upper - lower - 1).coerceAtLeast(0),
                    )
                }
            }
            GlassPanel(Modifier.weight(1f).fillMaxHeight()) {
                Box(Modifier.fillMaxSize()) {
                    ExhibitPlatform(Modifier.fillMaxSize())
                    InteractiveAtomModel(
                        element = element,
                        electrons = element.atomicNumber,
                        neutrons = neutrons,
                        modifier = Modifier.fillMaxSize().padding(bottom = 27.dp),
                        nucleusScale = 0.34f,
                    )
                    Text(
                        "${element.symbol}-$massNumber ATOM • ${element.atomicNumber} PROTONS • $neutrons NEUTRONS • ${element.atomicNumber} ELECTRONS",
                        fontSize = 8.sp,
                        color = ElectricCyan,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                    Column(Modifier.align(Alignment.BottomCenter), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("↔  DRAG NUCLEUS TO ROTATE", fontSize = 7.sp, color = ElectricCyan)
                        Text(
                            "Changing neutron count changes the isotope; ${element.atomicNumber} protons keep it ${element.name}.",
                            fontSize = 7.sp,
                            color = Color.White.copy(alpha = 0.72f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            GlassPanel(Modifier.width(220.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    PanelTitle("NUCLIDE DATA")
                    MetricRow("Nuclide", "${element.symbol}-$massNumber")
                    MetricRow("Atomic number", element.atomicNumber.toString())
                    MetricRow("Mass number", massNumber.toString())
                    MetricRow("Neutron number", neutrons.toString())
                    MetricRow(
                        "Natural abundance",
                        profile?.naturalAbundancePercent?.let { "${trimNumber(it)}%" } ?: "Not provided",
                    )
                    MetricRow(
                        "Stability",
                        when (profile?.stable) { true -> "Stable"; false -> "Radioactive"; null -> "Not asserted" },
                        valueColor = when (profile?.stable) { true -> PositiveGreen; false -> WarmAmber; null -> Color.White },
                    )
                    MetricRow("Nuclear spin", profile?.nuclearSpin ?: "Not provided")
                    Text(
                        if (profile?.naturalAbundancePercent != null) "Abundance and stability shown only where evaluated data is included."
                        else "This visualizer does not infer stability or abundance from neutron count alone.",
                        fontSize = 8.sp,
                        lineHeight = 11.sp,
                        color = WarmAmber,
                    )
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            nuclides.forEach { isotope ->
                val selected = isotope.massNumber == massNumber
                GlassPanel(
                    Modifier.weight(1f).fillMaxHeight().clickable { massNumber = isotope.massNumber },
                    accent = if (selected) ElectricCyan else Color.White.copy(alpha = 0.22f),
                    padding = 8.dp,
                ) {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Text("${element.symbol}-${isotope.massNumber}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("N ${isotope.massNumber - element.atomicNumber}", fontSize = 7.sp, color = NeutronBlue)
                            Text(isotope.naturalAbundancePercent?.let { "${trimNumber(it)}%" } ?: "—", fontSize = 7.sp, color = if (selected) ElectricCyan else Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun IonBuilderScreen(element: ElementProperties, onBack: () -> Unit) {
    val commonCharges = remember(element.atomicNumber) {
        parseOxidationStates(element).filter { it != 0 && it <= element.atomicNumber }.ifEmpty { listOf(0) }
    }
    // Neutral must always remain in range.  The old builder could create an invalid
    // slider range for elements whose listed oxidation states were all positive.
    val minimumCharge = minOf(0, commonCharges.minOrNull() ?: 0).coerceAtLeast(-4)
    val maximumCharge = maxOf(0, commonCharges.maxOrNull() ?: 0, 4).coerceAtMost(element.atomicNumber)
    var charge by remember(element.atomicNumber) { mutableIntStateOf(defaultIonCharge(element).coerceIn(minimumCharge, maximumCharge)) }
    var running by remember(element.atomicNumber) { mutableStateOf(true) }
    val electrons = element.atomicNumber - charge
    val shells = ionShellPopulation(element, electrons)
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("${element.name.uppercase()} (${element.symbol})", "ION BUILDER • ADD OR REMOVE ELECTRONS", onBack)
        Row(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 13.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GlassPanel(Modifier.width(205.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    PanelTitle("ION CONTROLS")
                    Text(ionNotation(element.symbol, charge), fontSize = 43.sp, color = ElectricCyan)
                    MetricRow("Net charge", signed(charge), valueColor = WarmAmber)
                    MetricRow("Protons", element.atomicNumber.toString(), valueColor = ProtonRed)
                    MetricRow("Electrons", electrons.toString(), valueColor = ElectricCyan)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ScienceButton("−  REMOVE e⁻", { charge = (charge + 1).coerceAtMost(maximumCharge) })
                        ScienceButton("+  ADD e⁻", { charge = (charge - 1).coerceAtLeast(minimumCharge) })
                    }
                    Text("ELECTRON CHANGE", fontSize = 8.sp, color = Color.White.copy(alpha = 0.62f))
                    Slider(
                        value = charge.toFloat(),
                        onValueChange = { charge = it.roundToInt().coerceIn(minimumCharge, maximumCharge) },
                        valueRange = minimumCharge.toFloat()..maximumCharge.toFloat(),
                        steps = (maximumCharge - minimumCharge - 1).coerceAtLeast(0),
                    )
                    ScienceButton("RESET NEUTRAL", { charge = 0.coerceIn(minimumCharge, maximumCharge) }, Modifier.fillMaxWidth())
                }
            }
            GlassPanel(Modifier.weight(1f).fillMaxHeight()) {
                Box(Modifier.fillMaxSize()) {
                    ExhibitPlatform(Modifier.fillMaxSize(), accent = if (charge > 0) WarmAmber else ElectricCyan)
                    InteractiveAtomModel(
                        element = element,
                        electrons = electrons,
                        modifier = Modifier.fillMaxSize().padding(bottom = 28.dp),
                        running = running,
                        nucleusScale = 0.4f,
                        shellsOverride = shells,
                    )
                    Text(
                        "$electrons ELECTRONS • ${shells.joinToString(" • ")}",
                        fontSize = 9.sp,
                        color = ElectricCyan,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                    Row(Modifier.align(Alignment.BottomCenter), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("↔  DRAG TO ROTATE", fontSize = 7.sp, color = ElectricCyan, modifier = Modifier.align(Alignment.CenterVertically))
                        ScienceButton(if (running) "PAUSE" else "RESUME", { running = !running })
                    }
                }
            }
            GlassPanel(Modifier.width(220.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    PanelTitle("CHARGE ACCOUNTING")
                    MetricRow("Nuclear charge", "+${element.atomicNumber}")
                    MetricRow("Electron charge", "−$electrons")
                    MetricRow("Net charge", signed(charge), valueColor = WarmAmber)
                    MetricRow("Ion type", when { charge > 0 -> "Cation"; charge < 0 -> "Anion"; else -> "Neutral" })
                    Text("COMMON OXIDATION STATES", fontSize = 8.sp, color = Color.White.copy(alpha = 0.55f))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        commonCharges.take(4).forEach { candidate ->
                            ScienceButton(signed(candidate), { charge = candidate.coerceIn(minimumCharge, maximumCharge) }, Modifier.weight(1f), selected = charge == candidate, accent = WarmAmber)
                        }
                    }
                    Text(
                        "Ion formation changes electron count, not the nucleus or elemental identity.",
                        fontSize = 8.sp,
                        lineHeight = 11.sp,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }
        }
        GlassPanel(Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 17.dp, vertical = 4.dp), padding = 8.dp) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                Text(ionReaction(element.symbol, charge), fontSize = 21.sp, color = WarmAmber, fontWeight = FontWeight.SemiBold)
                Text(
                    if (charge > 0) "A neutral ${element.name.lowercase()} atom loses $charge electron${if (charge == 1) "" else "s"}."
                    else if (charge < 0) "A neutral ${element.name.lowercase()} atom gains ${abs(charge)} electron${if (charge == -1) "" else "s"}."
                    else "Protons equal electrons in the neutral atom.",
                    fontSize = 8.sp,
                    color = Color.White.copy(alpha = 0.8f),
                )
                Text("ILLUSTRATIVE MODEL • NOT TO SCALE", fontSize = 7.sp, color = ElectricCyan)
            }
        }
    }
}

@Composable
internal fun BondBuilderScreen(element: ElementProperties, onBack: () -> Unit) {
    val library = remember(element.atomicNumber) { bondLibraryFor(element) }
    var selection by remember(element.atomicNumber) { mutableIntStateOf(0) }
    var showLabels by remember(element.atomicNumber) { mutableStateOf(true) }
    var showDensity by remember(element.atomicNumber) { mutableStateOf(true) }
    var moleculeResetKey by remember(element.atomicNumber) { mutableIntStateOf(0) }
    val species = library[selection.coerceIn(0, library.lastIndex)]
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("BOND BUILDER", "${element.name.uppercase()} • ELEMENT-SPECIFIC BONDING AND COMPOUNDS", onBack)
        Row(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 13.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GlassPanel(Modifier.width(215.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PanelTitle("${element.symbol} BOND LIBRARY")
                    Text("Common examples where established • unknowns identified", fontSize = 7.sp, color = Color.White.copy(alpha = 0.58f))
                    library.forEachIndexed { index, candidate ->
                        ScienceButton(
                            "${formulaWithSubscripts(candidate.formula)}  ${candidate.name}",
                            { selection = index },
                            Modifier.fillMaxWidth().weight(1f),
                            selected = selection == index,
                        )
                    }
                    HorizontalDivider(color = ElectricCyan.copy(alpha = 0.2f))
                    Text("Selected element: ${element.atomicNumber} • ${element.name}", fontSize = 7.sp, color = ElectricCyan)
                }
            }
            GlassPanel(Modifier.weight(1f).fillMaxHeight()) {
                Box(Modifier.fillMaxSize()) {
                    ExhibitPlatform(Modifier.fillMaxSize())
                    MoleculeVisual(
                        species = species,
                        modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
                        showElectronDensity = showDensity,
                        resetKey = moleculeResetKey,
                    )
                    Text(
                        "${species.name.uppercase()} • ${formulaWithSubscripts(species.formula)}",
                        fontSize = 10.sp,
                        color = ElectricCyan,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                    if (showLabels) {
                        Text(
                            if (species.bondAngle == "Not applicable") species.molecularGeometry else "BOND ANGLE ${species.bondAngle}",
                            fontSize = 8.sp,
                            color = WarmAmber,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    Row(Modifier.align(Alignment.BottomCenter), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        ScienceButton(if (showLabels) "HIDE LABELS" else "LABELS", { showLabels = !showLabels }, selected = showLabels)
                        ScienceButton(if (showDensity) "HIDE CLOUD" else "ELECTRON DENSITY", { showDensity = !showDensity }, selected = showDensity)
                        ScienceButton("RESET VIEW", {
                            showLabels = true
                            showDensity = true
                            moleculeResetKey += 1
                        })
                    }
                }
            }
            GlassPanel(Modifier.width(230.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    PanelTitle("BOND DATA")
                    MetricRow("Formula", formulaWithSubscripts(species.formula))
                    MetricRow("Bond character", species.bondType)
                    MetricRow("Geometry", species.molecularGeometry)
                    MetricRow("Electron geometry", species.electronGeometry)
                    MetricRow("Bond angle", species.bondAngle)
                    MetricRow("Polarity", species.polarity, valueColor = WarmAmber)
                    HorizontalDivider(color = ElectricCyan.copy(alpha = 0.2f))
                    Text(species.note, fontSize = 8.sp, lineHeight = 11.sp, color = Color.White.copy(alpha = 0.82f))
                    Text("↔ DRAG TO ROTATE • PINCHING IS RESERVED FOR THE PRODUCTION 3D CAMERA", fontSize = 6.5.sp, color = ElectricCyan)
                }
            }
        }
    }
}

@Composable
internal fun StatesOfMatterScreen(element: ElementProperties, onBack: () -> Unit) {
    val initial = remember(element.atomicNumber) { initialMatterTemperatureF(element) }
    var temperatureF by remember(element.atomicNumber) { mutableFloatStateOf(initial.toFloat()) }
    var running by remember(element.atomicNumber) { mutableStateOf(true) }
    var plasmaSelected by remember(element.atomicNumber) { mutableStateOf(false) }
    val calculatedPhase = phaseAtFahrenheit(element, temperatureF.toDouble())
    val phase = if (plasmaSelected) MatterPhase.PLASMA else calculatedPhase
    val meltingF = element.meltingPointFahrenheit()
    val boilingF = element.boilingPointFahrenheit()
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("STATES OF MATTER LAB", "${element.name.uppercase()} (${element.symbol}) AT 1 ATM • PHASE AND PARTICLE MOTION", onBack)
        Row(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 13.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GlassPanel(Modifier.width(215.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    PanelTitle("TEMPERATURE")
                    Text("${temperatureF.roundToInt()} °F", fontSize = 35.sp, color = if (phase == MatterPhase.SOLID) NeutronBlue else WarmAmber)
                    Slider(
                        value = temperatureF,
                        onValueChange = { temperatureF = it; plasmaSelected = false },
                        valueRange = -459f..10_000f,
                    )
                    MetricRow("Pressure", "1.00 atm")
                    MetricRow("State", phase.name, valueColor = phaseColor(phase))
                    MetricRow("Melting point", formatFahrenheit(meltingF))
                    MetricRow("Boiling point", formatFahrenheit(boilingF))
                    Text(
                        "Temperature is shown in Fahrenheit. ${matterBoundaryNote(element)}",
                        fontSize = 7.sp,
                        lineHeight = 10.sp,
                        color = Color.White.copy(alpha = 0.68f),
                    )
                }
            }
            GlassPanel(Modifier.weight(1f).fillMaxHeight()) {
                Box(Modifier.fillMaxSize()) {
                    MatterParticles(element, phase, Modifier.fillMaxSize().padding(top = 30.dp, bottom = 32.dp), running)
                    Text(phase.name, fontSize = 32.sp, color = phaseColor(phase), modifier = Modifier.align(Alignment.TopCenter))
                    Row(Modifier.align(Alignment.BottomCenter), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        ScienceButton(if (running) "PAUSE PARTICLES" else "RESUME", { running = !running })
                        Text(phaseExplanation(phase), fontSize = 7.sp, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
            GlassPanel(Modifier.width(230.dp).fillMaxHeight()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    PanelTitle("SELECTED SUBSTANCE")
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(52.dp).background(element.visualColor().copy(alpha = 0.44f), RoundedCornerShape(9.dp))
                                .border(1.dp, WarmAmber.copy(alpha = 0.72f), RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(element.atomicNumber.toString(), fontSize = 7.sp)
                                Text(element.symbol, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column { Text(element.name, fontSize = 15.sp); Text(element.category, fontSize = 8.sp, color = ElectricCyan) }
                    }
                    MetricRow("Phase now", phase.name, valueColor = phaseColor(phase))
                    MetricRow("Density", established(element.densityGPerCm3, "g/cm³"))
                    MetricRow("Standard state", established(element.standardState))
                    MetricRow("Atomic mass", "${element.atomicMass} u")
                    Text(
                        "Temperature changes average kinetic energy. The particles remain ${element.name.lowercase()} atoms during a phase change.",
                        fontSize = 8.sp,
                        lineHeight = 11.sp,
                        color = Color.White.copy(alpha = 0.82f),
                    )
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().height(62.dp).padding(horizontal = 18.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf(MatterPhase.SOLID, MatterPhase.LIQUID, MatterPhase.GAS, MatterPhase.PLASMA).forEach { candidate ->
                val available = matterPhaseAvailableAtOneAtmosphere(element, candidate)
                ScienceButton(
                    if (available) candidate.name else "${candidate.name} • N/A AT 1 ATM",
                    {
                        if (available) {
                            when (candidate) {
                                MatterPhase.SOLID -> temperatureF = ((listOfNotNull(meltingF, boilingF).minOrNull() ?: 100.0) - 100.0).coerceAtLeast(-459.0).toFloat()
                                MatterPhase.LIQUID -> temperatureF = if (meltingF != null && boilingF != null && boilingF > meltingF) ((meltingF + boilingF) / 2.0).toFloat() else 70f
                                MatterPhase.GAS -> temperatureF = ((boilingF ?: 200.0) + 100.0).coerceAtMost(9_000.0).toFloat()
                                MatterPhase.PLASMA -> temperatureF = 10_000f
                                MatterPhase.UNKNOWN -> Unit
                            }
                            plasmaSelected = candidate == MatterPhase.PLASMA
                        }
                    },
                    Modifier.weight(1f),
                    selected = phase == candidate,
                    accent = phaseColor(candidate),
                )
            }
        }
    }
}

@Composable
internal fun CompareElementsScreen(
    primary: ElementProperties,
    comparison: ElementProperties,
    onComparisonChanged: (ElementProperties) -> Unit,
    onSwap: () -> Unit,
    onBack: () -> Unit,
) {
    val quickChoices = remember(primary.atomicNumber) {
        listOf(1, 6, 8, 13, 26, 29, 47, 79, 92, primary.atomicNumber).distinct().map(::elementByAtomicNumber)
    }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("COMPARE ELEMENTS", "SIDE-BY-SIDE SCIENTIFIC PROPERTIES", onBack)
        Row(
            Modifier.fillMaxWidth().height(145.dp).padding(horizontal = 14.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            ElementComparisonSpecimen(primary, Modifier.weight(1f), ElectricCyan)
            Column(Modifier.width(64.dp).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("⇄", fontSize = 30.sp, color = ElectricCyan, modifier = Modifier.clickable(onClick = onSwap))
                Text("SWAP", fontSize = 7.sp, color = ElectricCyan)
            }
            ElementComparisonSpecimen(comparison, Modifier.weight(1f), WarmAmber)
        }
        GlassPanel(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp), padding = 10.dp) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                ComparisonRow("Atomic number", primary.atomicNumber.toString(), comparison.atomicNumber.toString())
                ComparisonRow("Standard atomic weight", "${primary.atomicMass} u", "${comparison.atomicMass} u")
                ComparisonRow("Category", primary.category, comparison.category)
                ComparisonRow("Density", established(primary.densityGPerCm3, "g/cm³"), established(comparison.densityGPerCm3, "g/cm³"), true)
                ComparisonRow("Melting point", formatFahrenheit(primary.meltingPointFahrenheit()), formatFahrenheit(comparison.meltingPointFahrenheit()), true)
                ComparisonRow("Boiling point", formatFahrenheit(primary.boilingPointFahrenheit()), formatFahrenheit(comparison.boilingPointFahrenheit()), true)
                ComparisonRow("Electronegativity", established(primary.electronegativity), established(comparison.electronegativity), true)
                ComparisonRow("Electron configuration", primary.electronConfiguration, comparison.electronConfiguration)
                ComparisonRow("Common oxidation states", established(primary.oxidationStates), established(comparison.oxidationStates))
                ComparisonRow("Standard state", established(primary.standardState), established(comparison.standardState))
            }
        }
        Row(
            Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 14.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("COMPARE ${primary.symbol} WITH", fontSize = 8.sp, color = ElectricCyan)
            quickChoices.filter { it.atomicNumber != primary.atomicNumber }.forEach { element ->
                ScienceButton(
                    "${element.symbol}  ${element.name}",
                    { onComparisonChanged(element) },
                    selected = element.atomicNumber == comparison.atomicNumber,
                    accent = if (element.atomicNumber == comparison.atomicNumber) WarmAmber else ElectricCyan,
                )
            }
            ScienceButton("SWAP", onSwap, accent = WarmAmber)
        }
    }
}

@Composable
private fun ElementComparisonSpecimen(element: ElementProperties, modifier: Modifier, accent: Color) {
    GlassPanel(modifier.fillMaxHeight(), accent = accent, padding = 8.dp) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.width(95.dp)) {
                Text(element.atomicNumber.toString(), fontSize = 10.sp, color = accent)
                Text(element.symbol, fontSize = 35.sp, fontWeight = FontWeight.Black)
                Text(element.name.uppercase(), fontSize = 9.sp, color = accent)
            }
            ElementSpecimen(element, Modifier.weight(1f).fillMaxHeight(), accent)
            Text("↔ DRAG", fontSize = 6.sp, color = accent)
        }
    }
}

@Composable
private fun ComparisonRow(label: String, left: String, right: String, showBars: Boolean = false) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(left, fontSize = 8.sp, color = ElectricCyan, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (showBars) LinearProgressIndicator(progress = { comparisonProgress(left, right) }, modifier = Modifier.fillMaxWidth(0.62f).height(2.dp), color = ElectricCyan)
        }
        Text(label.uppercase(), fontSize = 7.sp, color = Color.White.copy(alpha = 0.66f), textAlign = TextAlign.Center, modifier = Modifier.width(150.dp))
        Column(Modifier.weight(1f)) {
            Text(right, fontSize = 8.sp, color = WarmAmber, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (showBars) LinearProgressIndicator(progress = { comparisonProgress(right, left) }, modifier = Modifier.fillMaxWidth(0.62f).height(2.dp), color = WarmAmber)
        }
    }
}

private fun comparisonProgress(value: String, other: String): Float {
    val first = Regex("-?\\d+(?:\\.\\d+)?").find(value)?.value?.toFloatOrNull() ?: return 0f
    val second = Regex("-?\\d+(?:\\.\\d+)?").find(other)?.value?.toFloatOrNull() ?: return 0f
    val maximum = max(abs(first), abs(second)).coerceAtLeast(0.0001f)
    return (abs(first) / maximum).coerceIn(0f, 1f)
}

private fun signed(value: Int): String = when {
    value > 0 -> "+$value"
    else -> value.toString()
}

private fun ionReaction(symbol: String, charge: Int): String = when {
    charge > 0 -> "$symbol → ${ionNotation(symbol, charge)} + ${if (charge == 1) "" else charge}e⁻"
    charge < 0 -> "$symbol + ${if (charge == -1) "" else abs(charge)}e⁻ → ${ionNotation(symbol, charge)}"
    else -> "$symbol • neutral atom"
}

private fun trimNumber(value: Double): String = if (value % 1.0 == 0.0) value.roundToInt().toString() else value.toString().trimEnd('0').trimEnd('.')

private fun phaseColor(phase: MatterPhase): Color = when (phase) {
    MatterPhase.SOLID -> NeutronBlue
    MatterPhase.LIQUID -> ElectricCyan
    MatterPhase.GAS -> WarmAmber
    MatterPhase.PLASMA -> Color(0xFFC56BFF)
    MatterPhase.UNKNOWN -> Color.Gray
}

private fun phaseExplanation(phase: MatterPhase): String = when (phase) {
    MatterPhase.SOLID -> "Ordered particles vibrate in place."
    MatterPhase.LIQUID -> "Close particles move and flow past one another."
    MatterPhase.GAS -> "Separated particles move freely and rapidly."
    MatterPhase.PLASMA -> "A high-energy ionized state containing charged particles."
    MatterPhase.UNKNOWN -> "A phase cannot be assigned from the available data."
}
