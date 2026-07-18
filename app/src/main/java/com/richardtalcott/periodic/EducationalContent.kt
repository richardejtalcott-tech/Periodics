package com.richardtalcott.periodic

import kotlin.math.abs

internal data class NuclideProfile(
    val massNumber: Int,
    val naturalAbundancePercent: Double? = null,
    val stable: Boolean? = null,
    val nuclearSpin: String? = null,
)

private val evaluatedNuclides = mapOf(
    1 to listOf(
        NuclideProfile(1, 99.9885, true, "1/2+"),
        NuclideProfile(2, 0.0115, true, "1+"),
        NuclideProfile(3, null, false, "1/2+"),
    ),
    6 to listOf(
        NuclideProfile(12, 98.94, true, "0+"),
        NuclideProfile(13, 1.06, true, "1/2−"),
        NuclideProfile(14, null, false, "0+"),
    ),
    8 to listOf(
        NuclideProfile(16, 99.757, true, "0+"),
        NuclideProfile(17, 0.038, true, "5/2+"),
        NuclideProfile(18, 0.205, true, "0+"),
    ),
    26 to listOf(
        NuclideProfile(54, 5.845, true, "0+"),
        NuclideProfile(56, 91.754, true, "0+"),
        NuclideProfile(57, 2.119, true, "1/2−"),
        NuclideProfile(58, 0.282, true, "0+"),
    ),
    29 to listOf(
        NuclideProfile(63, 69.15, true, "3/2−"),
        NuclideProfile(65, 30.85, true, "3/2−"),
    ),
)

internal fun nuclidesFor(element: ElementProperties): List<NuclideProfile> =
    evaluatedNuclides[element.atomicNumber] ?: run {
        val center = maxOf(element.atomicNumber, element.representativeMassNumber)
        ((center - 2)..(center + 2))
            .filter { it >= element.atomicNumber }
            .map { NuclideProfile(it) }
    }

internal data class BondSpecies(
    val formula: String,
    val name: String,
    val bondType: String,
    val molecularGeometry: String,
    val electronGeometry: String,
    val bondAngle: String,
    val polarity: String,
    val note: String,
)

private fun molecular(
    formula: String,
    name: String,
    geometry: String,
    electronGeometry: String,
    angle: String,
    polarity: String,
    note: String,
) = BondSpecies(formula, name, "Covalent", geometry, electronGeometry, angle, polarity, note)

private fun lattice(formula: String, name: String, note: String) = BondSpecies(
    formula = formula,
    name = name,
    bondType = "Predominantly ionic",
    molecularGeometry = "Extended crystal lattice",
    electronGeometry = "Not a discrete molecule",
    bondAngle = "Not applicable",
    polarity = "Ionic solid",
    note = note,
)

private fun metallic(symbol: String, name: String) = BondSpecies(
    formula = symbol,
    name = "$name metal",
    bondType = "Metallic",
    molecularGeometry = "Extended crystal lattice",
    electronGeometry = "Delocalized valence electrons",
    bondAngle = "Not applicable",
    polarity = "Not molecular",
    note = "Metal atoms are held in an extended lattice by delocalized metallic bonding.",
)

private val curatedBondLibraries = mapOf(
    1 to listOf(
        molecular("H2", "Hydrogen", "Linear", "Linear", "180°", "Nonpolar", "H–H single covalent bond."),
        molecular("H2O", "Water", "Bent", "Tetrahedral", "104.5°", "Polar", "Two O–H bonds and two lone pairs on oxygen."),
        molecular("HCl", "Hydrogen chloride", "Linear", "Linear", "180°", "Polar", "Polar H–Cl covalent bond."),
        molecular("CH4", "Methane", "Tetrahedral", "Tetrahedral", "109.5°", "Nonpolar", "Four equivalent C–H bonds."),
    ),
    6 to listOf(
        molecular("CO2", "Carbon dioxide", "Linear", "Linear", "180°", "Nonpolar overall", "Two polar C=O bonds cancel by symmetry."),
        molecular("CH4", "Methane", "Tetrahedral", "Tetrahedral", "109.5°", "Nonpolar", "Four equivalent C–H bonds."),
        molecular("CO", "Carbon monoxide", "Linear", "Linear", "180°", "Polar", "A strongly bonded heteronuclear diatomic molecule."),
        molecular("C2H6", "Ethane", "Tetrahedral at carbon", "Tetrahedral", "109.5°", "Nearly nonpolar", "A C–C single bond joins two tetrahedral carbon centers."),
    ),
    3 to listOf(
        metallic("Li", "Lithium"),
        lattice("Li2O", "Lithium oxide", "A common Li⁺/O²⁻ ionic lattice."),
        lattice("LiCl", "Lithium chloride", "A common lithium halide built from Li⁺ and Cl⁻."),
        lattice("LiH", "Lithium hydride", "An ionic saline hydride containing Li⁺ and H⁻."),
    ),
    4 to listOf(
        metallic("Be", "Beryllium"),
        lattice("BeO", "Beryllium oxide", "A refractory solid with substantial covalent character."),
        molecular("BeCl2", "Beryllium chloride", "Linear monomer", "Linear", "180°", "Nonpolar monomer", "Solid BeCl₂ is polymeric; the gas contains linear monomers."),
        molecular("BeH2", "Beryllium hydride", "Polymeric solid", "Electron-deficient", "Variable", "Not molecular in solid", "Solid BeH₂ contains bridging hydrides."),
    ),
    5 to listOf(
        lattice("B2O3", "Boron trioxide", "A covalent network-forming boron oxide."),
        molecular("BF3", "Boron trifluoride", "Trigonal planar", "Trigonal planar", "120°", "Nonpolar overall", "Three polar B–F bonds cancel by symmetry."),
        molecular("BCl3", "Boron trichloride", "Trigonal planar", "Trigonal planar", "120°", "Nonpolar overall", "An electron-deficient trigonal-planar molecule."),
        molecular("B2H6", "Diborane", "Bridged", "Electron-deficient", "Variable", "Nonpolar overall", "Contains two three-center, two-electron B–H–B bonds."),
    ),
    7 to listOf(
        molecular("N2", "Nitrogen", "Linear", "Linear", "180°", "Nonpolar", "Two nitrogen atoms share a triple bond."),
        molecular("NH3", "Ammonia", "Trigonal pyramidal", "Tetrahedral", "107°", "Polar", "Three N–H bonds and one lone pair."),
        molecular("NO", "Nitric oxide", "Linear", "Linear", "180°", "Polar", "Odd-electron diatomic molecule."),
        molecular("NO2", "Nitrogen dioxide", "Bent", "Trigonal planar", "About 134°", "Polar", "Bent odd-electron molecule with resonance."),
    ),
    8 to listOf(
        molecular("O2", "Oxygen", "Linear", "Linear", "180°", "Nonpolar", "O=O double bond in the Lewis model; ground-state O₂ is paramagnetic."),
        molecular("H2O", "Water", "Bent", "Tetrahedral", "104.5°", "Polar", "Two O–H bonds and two lone pairs."),
        molecular("CO2", "Carbon dioxide", "Linear", "Linear", "180°", "Nonpolar overall", "Two polar C=O bonds cancel by symmetry."),
        lattice("Fe2O3", "Iron(III) oxide", "An extended Fe³⁺/O²⁻ solid; a formula unit is shown rather than a molecule."),
    ),
    9 to listOf(
        molecular("F2", "Fluorine", "Linear", "Linear", "180°", "Nonpolar", "A single F–F bond joins two fluorine atoms."),
        molecular("HF", "Hydrogen fluoride", "Linear", "Linear", "180°", "Strongly polar", "Highly polar H–F bond; condensed HF forms hydrogen-bonded chains."),
        molecular("OF2", "Oxygen difluoride", "Bent", "Tetrahedral", "About 103°", "Polar", "Oxygen is positive relative to fluorine in OF₂."),
        lattice("NaF", "Sodium fluoride", "A common Na⁺/F⁻ ionic lattice."),
    ),
    11 to listOf(
        metallic("Na", "Sodium"),
        lattice("NaCl", "Sodium chloride", "The familiar rock-salt Na⁺/Cl⁻ lattice."),
        lattice("Na2O", "Sodium oxide", "An ionic solid containing Na⁺ and O²⁻."),
        lattice("NaH", "Sodium hydride", "A saline hydride containing Na⁺ and H⁻."),
    ),
    12 to listOf(
        metallic("Mg", "Magnesium"),
        lattice("MgO", "Magnesium oxide", "A high-melting Mg²⁺/O²⁻ ionic lattice."),
        lattice("MgCl2", "Magnesium chloride", "A common magnesium halide."),
        lattice("MgH2", "Magnesium hydride", "A metal hydride used in hydrogen-storage research."),
    ),
    13 to listOf(
        metallic("Al", "Aluminium"),
        lattice("Al2O3", "Aluminium oxide", "Corundum is an extended Al³⁺/O²⁻ solid."),
        molecular("AlCl3", "Aluminium chloride", "Dimeric or extended", "Trigonal planar monomer", "120° monomer", "Lewis acidic", "AlCl₃ forms Al₂Cl₆ dimers in molecular phases."),
        lattice("AlN", "Aluminium nitride", "A covalent-ionic nitride with a wurtzite lattice."),
    ),
    14 to listOf(
        lattice("SiO2", "Silicon dioxide", "A three-dimensional covalent network rather than discrete SiO₂ molecules."),
        molecular("SiCl4", "Silicon tetrachloride", "Tetrahedral", "Tetrahedral", "109.5°", "Nonpolar overall", "Four polar Si–Cl bonds cancel by tetrahedral symmetry."),
        molecular("SiH4", "Silane", "Tetrahedral", "Tetrahedral", "109.5°", "Nonpolar", "The silicon analogue of methane."),
        lattice("SiC", "Silicon carbide", "A hard extended covalent solid."),
    ),
    15 to listOf(
        molecular("P4", "White phosphorus", "Tetrahedral cluster", "Three-coordinate phosphorus", "60° P–P–P", "Nonpolar", "P₄ is a molecular allotrope; other phosphorus allotropes are extended solids."),
        molecular("P4O10", "Phosphorus pentoxide", "Cage", "Tetrahedral at phosphorus", "About 109.5°", "Polar bonds", "The molecular formula is P₄O₁₀; P₂O₅ is its empirical formula."),
        molecular("PCl3", "Phosphorus trichloride", "Trigonal pyramidal", "Tetrahedral", "About 100°", "Polar", "Three P–Cl bonds and one lone pair."),
        molecular("PH3", "Phosphine", "Trigonal pyramidal", "Tetrahedral", "93.5°", "Weakly polar", "Three P–H bonds and one lone pair."),
    ),
    16 to listOf(
        molecular("S8", "Sulfur", "Puckered ring", "Two-coordinate sulfur", "About 108°", "Nonpolar", "The common molecular form is a crown-shaped S₈ ring."),
        molecular("SO2", "Sulfur dioxide", "Bent", "Trigonal planar", "About 119°", "Polar", "Bent molecule with resonance-delocalized S–O bonding."),
        molecular("SO3", "Sulfur trioxide", "Trigonal planar", "Trigonal planar", "120°", "Nonpolar overall", "Three equivalent S–O bonds in the gas-phase monomer."),
        molecular("H2S", "Hydrogen sulfide", "Bent", "Tetrahedral", "About 92°", "Polar", "Two S–H bonds and two lone pairs."),
    ),
    17 to listOf(
        molecular("Cl2", "Chlorine", "Linear", "Linear", "180°", "Nonpolar", "A single Cl–Cl bond joins two chlorine atoms."),
        molecular("HCl", "Hydrogen chloride", "Linear", "Linear", "180°", "Polar", "A strongly polar H–Cl bond."),
        lattice("NaCl", "Sodium chloride", "A common chloride in the rock-salt lattice."),
        molecular("Cl2O", "Dichlorine monoxide", "Bent", "Tetrahedral at oxygen", "About 111°", "Polar", "A bent Cl–O–Cl molecule."),
    ),
    19 to listOf(
        metallic("K", "Potassium"),
        lattice("KCl", "Potassium chloride", "A common K⁺/Cl⁻ ionic lattice."),
        lattice("K2O", "Potassium oxide", "An ionic oxide containing K⁺ and O²⁻."),
        lattice("KH", "Potassium hydride", "A saline hydride containing K⁺ and H⁻."),
    ),
    20 to listOf(
        metallic("Ca", "Calcium"),
        lattice("CaO", "Calcium oxide", "Quicklime is an extended Ca²⁺/O²⁻ lattice."),
        lattice("CaCl2", "Calcium chloride", "A common calcium halide."),
        lattice("CaCO3", "Calcium carbonate", "An ionic solid containing Ca²⁺ and carbonate ions."),
    ),
    22 to listOf(
        metallic("Ti", "Titanium"),
        lattice("TiO2", "Titanium dioxide", "A common titanium(IV) oxide with rutile and other polymorphs."),
        molecular("TiCl4", "Titanium tetrachloride", "Tetrahedral", "Tetrahedral", "109.5°", "Nonpolar overall", "A volatile molecular titanium halide."),
        lattice("TiN", "Titanium nitride", "A hard interstitial nitride with mixed bonding."),
    ),
    24 to listOf(
        metallic("Cr", "Chromium"),
        lattice("Cr2O3", "Chromium(III) oxide", "A stable protective chromium oxide."),
        molecular("CrO3", "Chromium trioxide", "Extended chains in solid", "Tetrahedral chromium", "Variable", "Polar solid", "Chromium(VI) oxide forms an extended solid structure."),
        lattice("CrCl3", "Chromium(III) chloride", "A layered chromium(III) halide."),
    ),
    25 to listOf(
        metallic("Mn", "Manganese"),
        lattice("MnO2", "Manganese dioxide", "A common manganese(IV) oxide with several polymorphs."),
        lattice("MnO", "Manganese(II) oxide", "An Mn²⁺/O²⁻ ionic lattice."),
        lattice("KMnO4", "Potassium permanganate", "An ionic solid containing K⁺ and tetrahedral permanganate ions."),
    ),
    26 to listOf(
        lattice("FeO", "Iron(II) oxide", "Common iron(II) oxide; an extended non-stoichiometric ionic solid."),
        lattice("Fe2O3", "Iron(III) oxide", "Hematite structure with Fe³⁺ and O²⁻ in an extended lattice."),
        lattice("Fe3O4", "Iron(II,III) oxide", "Magnetite contains both Fe²⁺ and Fe³⁺ in an inverse spinel lattice."),
        lattice("FeCl3", "Iron(III) chloride", "A common iron(III) halide; bonding has ionic and covalent character."),
    ),
    27 to listOf(
        metallic("Co", "Cobalt"),
        lattice("CoO", "Cobalt(II) oxide", "An extended Co²⁺/O²⁻ solid."),
        lattice("Co3O4", "Cobalt(II,III) oxide", "A mixed-valence spinel oxide."),
        lattice("CoCl2", "Cobalt(II) chloride", "A common cobalt(II) halide."),
    ),
    28 to listOf(
        metallic("Ni", "Nickel"),
        lattice("NiO", "Nickel(II) oxide", "An antiferromagnetic Ni²⁺/O²⁻ solid."),
        lattice("NiCl2", "Nickel(II) chloride", "A common layered nickel halide."),
        lattice("NiS", "Nickel sulfide", "A family of extended nickel sulfide phases."),
    ),
    29 to listOf(
        lattice("Cu2O", "Copper(I) oxide", "Cuprite lattice containing Cu⁺ and O²⁻."),
        lattice("CuO", "Copper(II) oxide", "Extended copper(II) oxide solid."),
        lattice("CuCl2", "Copper(II) chloride", "Copper(II) halide with an extended coordination structure in the solid."),
        lattice("CuSO4", "Copper(II) sulfate", "Ionic compound containing Cu²⁺ and sulfate ions."),
    ),
    30 to listOf(
        metallic("Zn", "Zinc"),
        lattice("ZnO", "Zinc oxide", "A common wurtzite zinc oxide."),
        lattice("ZnCl2", "Zinc chloride", "A zinc halide with appreciable covalent character."),
        lattice("ZnS", "Zinc sulfide", "An extended solid occurring as sphalerite or wurtzite."),
    ),
    35 to listOf(
        molecular("Br2", "Bromine", "Linear", "Linear", "180°", "Nonpolar", "The elemental liquid consists of Br₂ molecules."),
        molecular("HBr", "Hydrogen bromide", "Linear", "Linear", "180°", "Polar", "A polar H–Br covalent bond."),
        lattice("KBr", "Potassium bromide", "A common K⁺/Br⁻ ionic lattice."),
        molecular("BrF3", "Bromine trifluoride", "T-shaped", "Trigonal bipyramidal", "About 86° and 172°", "Polar", "Two equatorial lone pairs produce a T-shaped molecule."),
    ),
    36 to listOf(
        molecular("KrF2", "Krypton difluoride", "Linear", "Trigonal bipyramidal electron domains", "180°", "Nonpolar overall", "A rare but established krypton compound."),
    ),
    47 to listOf(
        metallic("Ag", "Silver"),
        lattice("Ag2O", "Silver(I) oxide", "An extended silver(I) oxide solid."),
        lattice("AgCl", "Silver chloride", "A photosensitive Ag⁺/Cl⁻ lattice."),
        lattice("AgNO3", "Silver nitrate", "An ionic solid containing Ag⁺ and nitrate ions."),
    ),
    53 to listOf(
        molecular("I2", "Iodine", "Linear", "Linear", "180°", "Nonpolar", "The elemental solid and vapor contain I₂ molecules."),
        molecular("HI", "Hydrogen iodide", "Linear", "Linear", "180°", "Polar", "A polar H–I bond."),
        lattice("KI", "Potassium iodide", "A common K⁺/I⁻ ionic lattice."),
        molecular("IF5", "Iodine pentafluoride", "Square pyramidal", "Octahedral", "About 90°", "Polar", "Five I–F bonds and one lone pair."),
    ),
    54 to listOf(
        molecular("XeF2", "Xenon difluoride", "Linear", "Trigonal bipyramidal electron domains", "180°", "Nonpolar overall", "Three equatorial lone pairs leave two axial Xe–F bonds."),
        molecular("XeF4", "Xenon tetrafluoride", "Square planar", "Octahedral", "90°", "Nonpolar overall", "Two lone pairs occupy opposite positions."),
        molecular("XeF6", "Xenon hexafluoride", "Distorted octahedral", "Seven electron domains", "Variable", "Polar", "Fluxional hypervalent xenon fluoride."),
    ),
    79 to listOf(
        metallic("Au", "Gold"),
        lattice("AuCl", "Gold(I) chloride", "An extended gold(I) chloride solid."),
        molecular("AuCl3", "Gold(III) chloride", "Dimeric", "Square planar at gold", "About 90°", "Polar", "Commonly exists as the dimer Au₂Cl₆."),
        lattice("Au2O3", "Gold(III) oxide", "A gold(III) oxide solid that decomposes on heating."),
    ),
    80 to listOf(
        metallic("Hg", "Mercury"),
        lattice("HgO", "Mercury(II) oxide", "An extended mercury(II) oxide solid."),
        molecular("HgCl2", "Mercury(II) chloride", "Linear monomer", "Linear", "180°", "Nonpolar monomer", "A molecular mercury(II) halide with linear vapor-phase molecules."),
        lattice("Hg2Cl2", "Mercury(I) chloride", "Calomel contains linear Hg₂²⁺ units and chloride ions."),
    ),
    82 to listOf(
        metallic("Pb", "Lead"),
        lattice("PbO", "Lead(II) oxide", "An extended lead(II) oxide solid."),
        lattice("PbO2", "Lead(IV) oxide", "A lead(IV) oxide used in lead-acid batteries."),
        lattice("PbS", "Lead(II) sulfide", "Galena has a rock-salt Pb²⁺/S²⁻ lattice."),
    ),
    92 to listOf(
        metallic("U", "Uranium"),
        lattice("UO2", "Uranium dioxide", "A fluorite-structure uranium(IV) oxide used as reactor fuel."),
        lattice("U3O8", "Triuranium octoxide", "A stable mixed-valence uranium oxide."),
        molecular("UF6", "Uranium hexafluoride", "Octahedral", "Octahedral", "90°", "Nonpolar overall", "A volatile molecular compound used in uranium enrichment."),
    ),
)

private fun gcd(a: Int, b: Int): Int = if (b == 0) abs(a) else gcd(b, a % b)

private fun empiricalFormula(symbol: String, cationCharge: Int, anionSymbol: String, anionChargeMagnitude: Int): String {
    val positive = maxOf(1, abs(cationCharge))
    val divisor = gcd(positive, anionChargeMagnitude)
    val cations = anionChargeMagnitude / divisor
    val anions = positive / divisor
    return buildString {
        append(symbol)
        if (cations > 1) append(cations)
        append(anionSymbol)
        if (anions > 1) append(anions)
    }
}

internal fun bondLibraryFor(element: ElementProperties): List<BondSpecies> {
    curatedBondLibraries[element.atomicNumber]?.let { return it }
    if (element.atomicNumber >= 104) {
        return listOf(BondSpecies(
            formula = element.symbol,
            name = "${element.name}: no established common compound",
            bondType = "Not experimentally established",
            molecularGeometry = "Not established",
            electronGeometry = "Not established",
            bondAngle = "Not established",
            polarity = "Not established",
            note = "Only very small numbers of short-lived atoms have been produced; this app does not invent a molecular structure.",
        ))
    }
    if (element.category.contains("Noble gas", ignoreCase = true)) {
        return listOf(BondSpecies(
            element.symbol,
            "Monatomic ${element.name}",
            "No conventional bond",
            "Monatomic",
            "Not applicable",
            "Not applicable",
            "Not applicable",
            "No common stable neutral compounds are included for ${element.name}.",
        ))
    }
    val positiveCharge = parseOxidationStates(element).filter { it > 0 }.minOrNull() ?: 1
    val oxide = empiricalFormula(element.symbol, positiveCharge, "O", 2)
    val chloride = empiricalFormula(element.symbol, positiveCharge, "Cl", 1)
    val fluoride = empiricalFormula(element.symbol, positiveCharge, "F", 1)
    return listOf(
        if (element.category.contains("metal", ignoreCase = true)) metallic(element.symbol, element.name)
        else lattice(oxide, "Representative ${element.name.lowercase()} oxide", "Stoichiometry uses the listed +$positiveCharge oxidation state. Verify phase-specific crystal structure in advanced references."),
        lattice(chloride, "Representative ${element.name.lowercase()} chloride", "A representative halide using the listed +$positiveCharge oxidation state."),
        lattice(fluoride, "Representative ${element.name.lowercase()} fluoride", "A representative fluoride using the listed +$positiveCharge oxidation state."),
    )
}

internal fun formulaWithSubscripts(formula: String): String {
    val subscripts = mapOf('0' to '₀', '1' to '₁', '2' to '₂', '3' to '₃', '4' to '₄', '5' to '₅', '6' to '₆', '7' to '₇', '8' to '₈', '9' to '₉')
    return formula.map { subscripts[it] ?: it }.joinToString("")
}

internal fun parseFormulaAtoms(formula: String): List<ElementProperties> {
    val atoms = mutableListOf<ElementProperties>()
    Regex("([A-Z][a-z]?)(\\d*)").findAll(formula).forEach { match ->
        val symbol = match.groupValues[1]
        val count = match.groupValues[2].toIntOrNull() ?: 1
        val element = ELEMENTS.firstOrNull { it.symbol == symbol } ?: return@forEach
        repeat(count.coerceAtMost(12)) { atoms += element }
    }
    return atoms.ifEmpty { listOf(elementByAtomicNumber(1)) }
}

internal fun conciseElementDescription(element: ElementProperties): String = when (element.atomicNumber) {
    1 -> "Hydrogen is the lightest element and the most abundant element in the universe."
    6 -> "Carbon forms an exceptional range of compounds and is central to known life."
    8 -> "Oxygen supports aerobic respiration and is a major component of Earth's crust and oceans."
    26 -> "Iron is a strong, workable transition metal central to steelmaking and biological oxygen transport."
    29 -> "Copper is a conductive transition metal widely used in electrical systems and alloys."
    else -> "${element.name} is classified as ${element.category.lowercase()} and has atomic number ${element.atomicNumber}."
}
