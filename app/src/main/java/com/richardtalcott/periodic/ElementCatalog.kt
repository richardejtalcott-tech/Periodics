package com.richardtalcott.periodic

import kotlin.math.roundToInt

internal data class TablePosition(val row: Int, val column: Int)

internal fun tablePosition(atomicNumber: Int): TablePosition = when {
    atomicNumber == 1 -> TablePosition(1, 1)
    atomicNumber == 2 -> TablePosition(1, 18)
    atomicNumber <= 10 -> TablePosition(2, listOf(1, 2, 13, 14, 15, 16, 17, 18)[atomicNumber - 3])
    atomicNumber <= 18 -> TablePosition(3, listOf(1, 2, 13, 14, 15, 16, 17, 18)[atomicNumber - 11])
    atomicNumber <= 36 -> TablePosition(4, atomicNumber - 18)
    atomicNumber <= 54 -> TablePosition(5, atomicNumber - 36)
    atomicNumber == 55 -> TablePosition(6, 1)
    atomicNumber == 56 -> TablePosition(6, 2)
    atomicNumber in 57..71 -> TablePosition(8, atomicNumber - 54)
    atomicNumber <= 86 -> TablePosition(6, atomicNumber - 68)
    atomicNumber == 87 -> TablePosition(7, 1)
    atomicNumber == 88 -> TablePosition(7, 2)
    atomicNumber in 89..103 -> TablePosition(9, atomicNumber - 86)
    else -> TablePosition(7, atomicNumber - 100)
}

internal fun kelvinToFahrenheit(kelvin: Double): Double = (kelvin - 273.15) * 9.0 / 5.0 + 32.0
internal fun fahrenheitToKelvin(fahrenheit: Double): Double = (fahrenheit - 32.0) * 5.0 / 9.0 + 273.15

internal fun ElementProperties.meltingPointFahrenheit(): Double? = meltingPointK.toDoubleOrNull()?.let(::kelvinToFahrenheit)
internal fun ElementProperties.boilingPointFahrenheit(): Double? = boilingPointK.toDoubleOrNull()?.let(::kelvinToFahrenheit)

internal fun formatFahrenheit(value: Double?): String = value?.let { "${it.roundToInt()} °F" } ?: "Not established"
internal fun established(value: String, unit: String = ""): String =
    if (value.isBlank()) "Not established" else if (unit.isBlank()) value else "$value $unit"

private val nobleCoreElectrons = mapOf("He" to 2, "Ne" to 10, "Ar" to 18, "Kr" to 36, "Xe" to 54, "Rn" to 86)

internal fun neutralShellPopulation(element: ElementProperties): List<Int> {
    val counts = IntArray(7)
    val configuration = element.electronConfiguration
    Regex("\\[([A-Z][a-z]?)\\]").find(configuration)?.groupValues?.get(1)?.let { core ->
        nobleCoreElectrons[core]?.let { coreElectrons ->
            neutralShellPopulation(elementByAtomicNumber(coreElectrons)).forEachIndexed { index, value -> counts[index] += value }
        }
    }
    Regex("([1-7])[spdf](\\d+)").findAll(configuration.substringAfter(']', configuration)).forEach { match ->
        counts[match.groupValues[1].toInt() - 1] += match.groupValues[2].toInt()
    }
    if (counts.sum() != element.atomicNumber) {
        return aufbauShellPopulation(element.atomicNumber)
    }
    return counts.toList().dropLastWhile { it == 0 }
}

private fun aufbauShellPopulation(electrons: Int): List<Int> {
    val subshells = listOf(
        1 to 2, 2 to 2, 2 to 6, 3 to 2, 3 to 6, 4 to 2, 3 to 10, 4 to 6,
        5 to 2, 4 to 10, 5 to 6, 6 to 2, 4 to 14, 5 to 10, 6 to 6, 7 to 2,
        5 to 14, 6 to 10, 7 to 6,
    )
    val shells = IntArray(7)
    var remaining = electrons.coerceAtLeast(0)
    subshells.forEach { (shell, capacity) ->
        if (remaining > 0) {
            val placed = minOf(capacity, remaining)
            shells[shell - 1] += placed
            remaining -= placed
        }
    }
    return shells.toList().dropLastWhile { it == 0 }
}

internal fun ionShellPopulation(element: ElementProperties, electronCount: Int): List<Int> {
    val target = electronCount.coerceAtLeast(0)
    val shells = neutralShellPopulation(element).toMutableList()
    while (shells.sum() > target) {
        val outer = shells.indexOfLast { it > 0 }
        if (outer < 0) break
        shells[outer] -= 1
    }
    while (shells.sum() < target) {
        val fallback = aufbauShellPopulation(shells.sum() + 1)
        while (shells.size < fallback.size) shells += 0
        val changed = fallback.indices.firstOrNull { fallback[it] > shells.getOrElse(it) { 0 } } ?: shells.lastIndex
        shells[changed] += 1
    }
    return shells.dropLastWhile { it == 0 }
}

internal fun parseOxidationStates(element: ElementProperties): List<Int> =
    Regex("[+-]?\\d+").findAll(element.oxidationStates)
        .mapNotNull { it.value.toIntOrNull() }
        .filter { it in -8..8 }
        .distinct()
        .toList()

internal fun defaultIonCharge(element: ElementProperties): Int = when (element.atomicNumber) {
    26 -> 3
    29 -> 2
    else -> parseOxidationStates(element).firstOrNull { it != 0 && it <= element.atomicNumber } ?: 0
}

internal fun ionNotation(symbol: String, charge: Int): String {
    if (charge == 0) return symbol
    val superscriptDigits = mapOf('0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴', '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹')
    val magnitude = kotlin.math.abs(charge)
    val number = if (magnitude == 1) "" else magnitude.toString().map { superscriptDigits[it] }.joinToString("")
    return symbol + number + if (charge > 0) "⁺" else "⁻"
}

internal enum class MatterPhase { SOLID, LIQUID, GAS, PLASMA, UNKNOWN }

internal fun phaseAtFahrenheit(element: ElementProperties, temperatureF: Double): MatterPhase {
    val temperatureK = fahrenheitToKelvin(temperatureF)
    val melting = element.meltingPointK.toDoubleOrNull()
    val boiling = element.boilingPointK.toDoubleOrNull()
    // Helium does not freeze at one atmosphere; it remains liquid below its
    // normal boiling point unless pressure is applied.
    if (element.atomicNumber == 2 && boiling != null) {
        return if (temperatureK < boiling) MatterPhase.LIQUID else MatterPhase.GAS
    }
    // Carbon sublimes at ordinary pressure; its liquid phase requires high pressure.
    if (element.atomicNumber == 6 && melting != null) {
        return if (temperatureK < melting) MatterPhase.SOLID else MatterPhase.GAS
    }
    // A reversed PubChem boundary identifies an element such as arsenic that
    // sublimes at approximately one atmosphere rather than forming a liquid.
    if (melting != null && boiling != null && boiling < melting) {
        return if (temperatureK < boiling) MatterPhase.SOLID else MatterPhase.GAS
    }
    if (melting != null && boiling != null && boiling >= melting) {
        return when {
            temperatureK < melting -> MatterPhase.SOLID
            temperatureK < boiling -> MatterPhase.LIQUID
            else -> MatterPhase.GAS
        }
    }
    if (temperatureF in 60.0..80.0) {
        return when {
            element.standardState.contains("Solid", ignoreCase = true) -> MatterPhase.SOLID
            element.standardState.contains("Liquid", ignoreCase = true) -> MatterPhase.LIQUID
            element.standardState.contains("Gas", ignoreCase = true) -> MatterPhase.GAS
            else -> MatterPhase.UNKNOWN
        }
    }
    return MatterPhase.UNKNOWN
}

internal fun initialMatterTemperatureF(element: ElementProperties): Double = 70.0

internal fun matterPhaseAvailableAtOneAtmosphere(element: ElementProperties, phase: MatterPhase): Boolean = when {
    phase == MatterPhase.PLASMA || phase == MatterPhase.GAS -> true
    element.atomicNumber == 2 && phase == MatterPhase.SOLID -> false
    element.atomicNumber == 6 && phase == MatterPhase.LIQUID -> false
    element.meltingPointK.toDoubleOrNull() != null &&
        element.boilingPointK.toDoubleOrNull() != null &&
        element.boilingPointK.toDouble() < element.meltingPointK.toDouble() &&
        phase == MatterPhase.LIQUID -> false
    else -> true
}

internal fun matterBoundaryNote(element: ElementProperties): String = when {
    element.atomicNumber == 2 -> "At 1 atm helium does not freeze; solid helium requires applied pressure."
    element.atomicNumber == 6 -> "At about 1 atm carbon sublimes; liquid carbon requires high pressure."
    element.meltingPointK.toDoubleOrNull() != null &&
        element.boilingPointK.toDoubleOrNull() != null &&
        element.boilingPointK.toDouble() < element.meltingPointK.toDouble() ->
        "At about 1 atm ${element.name} sublimes; a stable liquid range is not shown."
    else -> "Phase boundaries use the selected element's established values at about 1 atm."
}
