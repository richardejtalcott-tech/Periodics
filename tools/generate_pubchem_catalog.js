#!/usr/bin/env node

const fs = require("fs");

const sourcePath = process.argv[2];
const outputPath = process.argv[3];

if (!sourcePath || !outputPath) {
  throw new Error("Usage: generate_pubchem_catalog.js <pubchem.json> <ScientificCatalog.kt>");
}

const source = JSON.parse(fs.readFileSync(sourcePath, "utf8"));
const columns = source.Table.Columns.Column;
const rows = source.Table.Row.map(({ Cell }) => Object.fromEntries(columns.map((column, index) => [column, Cell[index] || ""])))

if (rows.length !== 118) {
  throw new Error(`Expected 118 PubChem rows, found ${rows.length}`);
}

const quote = value => JSON.stringify(String(value ?? ""));
const representativeMass = raw => Math.max(1, Math.round(Number.parseFloat(String(raw).replace(/[\[\]]/g, ""))));

let kotlin = `package com.richardtalcott.periodic

/**
 * Generated from the NIH PubChem Periodic Table PUG REST dataset.
 * Source: https://pubchem.ncbi.nlm.nih.gov/rest/pug/periodictable/JSON
 * Empty strings mean PubChem does not provide an established value.
 */
internal data class ElementProperties(
    val atomicNumber: Int,
    val symbol: String,
    val name: String,
    val atomicMass: String,
    val cpkHexColor: String,
    val electronConfiguration: String,
    val electronegativity: String,
    val atomicRadiusPm: String,
    val ionizationEnergyEv: String,
    val electronAffinityEv: String,
    val oxidationStates: String,
    val standardState: String,
    val meltingPointK: String,
    val boilingPointK: String,
    val densityGPerCm3: String,
    val category: String,
    val yearDiscovered: String,
    val representativeMassNumber: Int,
)

internal val ELEMENTS: List<ElementProperties> = listOf(
`;

kotlin += rows.map(row => `    ElementProperties(
        atomicNumber = ${row.AtomicNumber}, symbol = ${quote(row.Symbol)}, name = ${quote(row.Name)},
        atomicMass = ${quote(row.AtomicMass)}, cpkHexColor = ${quote(row.CPKHexColor)},
        electronConfiguration = ${quote(row.ElectronConfiguration)}, electronegativity = ${quote(row.Electronegativity)},
        atomicRadiusPm = ${quote(row.AtomicRadius)}, ionizationEnergyEv = ${quote(row.IonizationEnergy)},
        electronAffinityEv = ${quote(row.ElectronAffinity)}, oxidationStates = ${quote(row.OxidationStates)},
        standardState = ${quote(row.StandardState)}, meltingPointK = ${quote(row.MeltingPoint)},
        boilingPointK = ${quote(row.BoilingPoint)}, densityGPerCm3 = ${quote(row.Density)},
        category = ${quote(row.GroupBlock)}, yearDiscovered = ${quote(row.YearDiscovered)},
        representativeMassNumber = ${representativeMass(row.AtomicMass)},
    )`).join(",\n");

kotlin += `
)

internal fun elementByAtomicNumber(number: Int): ElementProperties =
    ELEMENTS[(number.coerceIn(1, 118)) - 1]
`;

fs.writeFileSync(outputPath, kotlin);
