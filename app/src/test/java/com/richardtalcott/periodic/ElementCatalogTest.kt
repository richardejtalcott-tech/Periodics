package com.richardtalcott.periodic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ElementCatalogTest {
  @Test fun containsAll118OfficialElements() {
    assertEquals(118, ELEMENT_NAMES.size)
    assertEquals(118, REPRESENTATIVE_MASS_NUMBERS.size)
    assertEquals("Hydrogen", ELEMENT_NAMES.first())
    assertEquals("Oganesson", ELEMENT_NAMES.last())
  }

  @Test fun propertyCatalogCoversEveryElement() {
    assertEquals(118, ELEMENT_PROPERTIES.size)
    assertEquals("65.38(2)", ELEMENT_PROPERTIES[29].atomicMass)
    assertEquals("[Ar] 3d10 4s2", ELEMENT_PROPERTIES[29].configuration)
  }

  @Test fun zincRegressionIsCorrect() {
    assertEquals("Zinc", ELEMENT_NAMES[29])
    assertEquals(65, REPRESENTATIVE_MASS_NUMBERS[29])
  }

  @Test fun representativeMassNeverHasFewerNucleonsThanProtons() {
    REPRESENTATIVE_MASS_NUMBERS.forEachIndexed { index, mass ->
      assertTrue("Invalid representative mass for atomic number ${index + 1}", mass >= index + 1)
    }
  }

  @Test fun everyKnownPhaseTemperatureIsPositiveKelvin() {
    ELEMENT_PROPERTIES.forEachIndexed { index, element ->
      listOf("melting" to element.meltingPoint, "boiling" to element.boilingPoint).forEach { (label, raw) ->
        assertTrue(
          "Invalid $label temperature for ${ELEMENT_NAMES[index]}: $raw",
          raw == "Unknown" || (raw.toFloatOrNull() != null && raw.toFloat() > 0f)
        )
      }
    }
  }

  @Test fun lowerBoilingValueOnlyOccursForDocumentedSublimationCase() {
    ELEMENT_PROPERTIES.forEachIndexed { index, element ->
      val melting = element.meltingPoint.toFloatOrNull()
      val boiling = element.boilingPoint.toFloatOrNull()
      if (melting != null && boiling != null && boiling < melting) {
        assertEquals("Unexpected phase-order anomaly for ${ELEMENT_NAMES[index]}", "Arsenic", ELEMENT_NAMES[index])
      }
    }
  }

  @Test fun criticallyVisibleMeltingPointAnchorsRemainCorrect() {
    assertEquals("14", ELEMENT_PROPERTIES[0].meltingPoint)    // Hydrogen
    assertEquals("1358", ELEMENT_PROPERTIES[28].meltingPoint) // Copper
    assertEquals("3695", ELEMENT_PROPERTIES[73].meltingPoint) // Tungsten
    assertEquals("234", ELEMENT_PROPERTIES[79].meltingPoint)  // Mercury
  }

  @Test fun allPropertyLabelsAndConfigurationsArePresent() {
    ELEMENT_PROPERTIES.forEachIndexed { index, element ->
      assertTrue("Missing category for ${ELEMENT_NAMES[index]}", element.category.isNotBlank())
      assertTrue("Missing configuration for ${ELEMENT_NAMES[index]}", element.configuration.isNotBlank())
      assertTrue("Missing mass for ${ELEMENT_NAMES[index]}", element.atomicMass.isNotBlank())
    }
  }
}
