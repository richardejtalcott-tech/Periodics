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

  @Test fun zincRegressionIsCorrect() {
    assertEquals("Zinc", ELEMENT_NAMES[29])
    assertEquals(65, REPRESENTATIVE_MASS_NUMBERS[29])
  }

  @Test fun representativeMassNeverHasFewerNucleonsThanProtons() {
    REPRESENTATIVE_MASS_NUMBERS.forEachIndexed { index, mass ->
      assertTrue("Invalid representative mass", mass >= index + 1)
    }
  }
}
