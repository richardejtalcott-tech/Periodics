package com.richardtalcott.periodic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ElementCatalogTest {
    @Test
    fun catalogContains118UniqueOfficialElementsInAtomicOrder() {
        assertEquals(118, ELEMENTS.size)
        assertEquals((1..118).toList(), ELEMENTS.map { it.atomicNumber })
        assertEquals(118, ELEMENTS.map { it.symbol }.toSet().size)
        assertEquals(118, ELEMENTS.map { it.name }.toSet().size)
        assertEquals("Hydrogen", ELEMENTS.first().name)
        assertEquals("Oganesson", ELEMENTS.last().name)
    }

    @Test
    fun everyElementHasAUniquePeriodicTablePosition() {
        val positions = ELEMENTS.map { tablePosition(it.atomicNumber) }
        assertEquals(118, positions.toSet().size)
        positions.forEach {
            assertTrue(it.row in 1..9)
            assertTrue(it.column in 1..18)
        }
    }

    @Test
    fun pubChemAnchorValuesRemainExact() {
        val hydrogen = elementByAtomicNumber(1)
        val iron = elementByAtomicNumber(26)
        val copper = elementByAtomicNumber(29)
        val tungsten = elementByAtomicNumber(74)
        val mercury = elementByAtomicNumber(80)

        assertEquals("1.0080", hydrogen.atomicMass)
        assertEquals("13.81", hydrogen.meltingPointK)
        assertEquals("55.84", iron.atomicMass)
        assertEquals("[Ar]4s2 3d6", iron.electronConfiguration)
        assertEquals("1811", iron.meltingPointK)
        assertEquals("3134", iron.boilingPointK)
        assertEquals("7.874", iron.densityGPerCm3)
        assertEquals("1357.77", copper.meltingPointK)
        assertEquals("3695", tungsten.meltingPointK)
        assertEquals("234.32", mercury.meltingPointK)
    }

    @Test
    fun establishedTemperaturesArePositiveKelvinAndMassesArePhysical() {
        ELEMENTS.forEach { element ->
            assertTrue("Mass number below proton count for ${element.name}", element.representativeMassNumber >= element.atomicNumber)
            listOf(element.meltingPointK, element.boilingPointK).filter { it.isNotBlank() }.forEach { raw ->
                assertTrue("Invalid temperature for ${element.name}: $raw", raw.toDouble() > 0.0)
            }
            assertFalse("Do not encode missing science as a fake value", listOf(
                element.atomicMass,
                element.electronConfiguration,
                element.meltingPointK,
                element.boilingPointK,
            ).any { it.equals("Unknown", ignoreCase = true) })
        }
    }

    @Test
    fun neutralShellPopulationsMatchAtomicNumbers() {
        ELEMENTS.forEach { element ->
            assertEquals("Wrong electron total for ${element.name}", element.atomicNumber, neutralShellPopulation(element).sum())
        }
        assertEquals(listOf(2, 8, 14, 2), neutralShellPopulation(elementByAtomicNumber(26)))
        assertEquals(listOf(2, 8, 18, 1), neutralShellPopulation(elementByAtomicNumber(29)))
    }

    @Test
    fun ionShellsAndNotationKeepChargeAccountingConsistent() {
        val iron = elementByAtomicNumber(26)
        assertEquals(listOf(2, 8, 13), ionShellPopulation(iron, 23))
        assertEquals(23, ionShellPopulation(iron, 23).sum())
        assertEquals("Fe³⁺", ionNotation("Fe", 3))
        assertEquals("O²⁻", ionNotation("O", -2))
        assertEquals("Na", ionNotation("Na", 0))
    }

    @Test
    fun roomTemperatureAndPhaseBoundariesUseTheSelectedElement() {
        assertEquals(MatterPhase.GAS, phaseAtFahrenheit(elementByAtomicNumber(1), 40.0))
        assertEquals(MatterPhase.GAS, phaseAtFahrenheit(elementByAtomicNumber(8), 70.0))
        assertEquals(MatterPhase.LIQUID, phaseAtFahrenheit(elementByAtomicNumber(35), 70.0))
        assertEquals(MatterPhase.SOLID, phaseAtFahrenheit(elementByAtomicNumber(26), 70.0))
        assertEquals(MatterPhase.LIQUID, phaseAtFahrenheit(elementByAtomicNumber(26), 3_000.0))
        assertEquals(MatterPhase.LIQUID, phaseAtFahrenheit(elementByAtomicNumber(80), 70.0))
    }

    @Test
    fun oneAtmosphereExceptionsAreNotRenderedAsImpossiblePhases() {
        val helium = elementByAtomicNumber(2)
        val carbon = elementByAtomicNumber(6)
        val arsenic = elementByAtomicNumber(33)
        assertFalse(matterPhaseAvailableAtOneAtmosphere(helium, MatterPhase.SOLID))
        assertEquals(MatterPhase.LIQUID, phaseAtFahrenheit(helium, -458.0))
        assertFalse(matterPhaseAvailableAtOneAtmosphere(carbon, MatterPhase.LIQUID))
        assertFalse(matterPhaseAvailableAtOneAtmosphere(arsenic, MatterPhase.LIQUID))
        assertEquals(MatterPhase.SOLID, phaseAtFahrenheit(arsenic, 1_000.0))
        assertEquals(MatterPhase.GAS, phaseAtFahrenheit(arsenic, 1_300.0))
    }

    @Test
    fun isotopeChangesNeutronsWithoutChangingElementIdentity() {
        val iron = elementByAtomicNumber(26)
        val isotopes = nuclidesFor(iron)
        assertEquals(listOf(54, 56, 57, 58), isotopes.map { it.massNumber })
        assertEquals(listOf(28, 30, 31, 32), isotopes.map { it.massNumber - iron.atomicNumber })
        assertTrue(isotopes.first { it.massNumber == 56 }.stable == true)
    }

    @Test
    fun bondLibrariesAreElementSpecificAndContainTheSelectedElement() {
        val hydrogen = elementByAtomicNumber(1)
        val oxygen = elementByAtomicNumber(8)
        val iron = elementByAtomicNumber(26)
        val copper = elementByAtomicNumber(29)
        assertNotEquals(bondLibraryFor(hydrogen).map { it.formula }, bondLibraryFor(oxygen).map { it.formula })
        assertNotEquals(bondLibraryFor(iron).map { it.formula }, bondLibraryFor(copper).map { it.formula })
        ELEMENTS.forEach { element ->
            val library = bondLibraryFor(element)
            assertTrue("No bond entries for ${element.name}", library.isNotEmpty())
            assertTrue(
                "Bond entry does not include ${element.symbol}",
                library.all { species -> parseFormulaAtoms(species.formula).any { it.atomicNumber == element.atomicNumber } },
            )
        }
    }

    @Test
    fun fahrenheitConversionsRoundTrip() {
        listOf(-459.0, 32.0, 70.0, 212.0, 3_000.0).forEach { fahrenheit ->
            val roundTrip = kelvinToFahrenheit(fahrenheitToKelvin(fahrenheit))
            assertEquals(fahrenheit, roundTrip, 0.000001)
        }
    }
}
