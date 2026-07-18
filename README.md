# Periodic 4 — Approved Vision Rebuild

Periodic is a landscape Android science laboratory for exploring all 118 elements. This branch is the isolated v4 replacement build; `main` and the earlier working app remain untouched.

## Approved interfaces

- Cinematic atom launch screen
- Straight-on, movable and tiltable periodic table inside a true Filament/SceneView 3D laboratory
- Complete element profile using the element that was actually selected
- Interactive atom explorer with oval energy-level paths and a rotatable nucleus
- Isotope lab that changes neutrons without changing elemental identity
- Crash-safe ion builder with explicit proton/electron/net-charge accounting
- Element-specific bond and compound library with draggable molecule models
- Fahrenheit states-of-matter lab using the selected element's own phase data
- Dynamic side-by-side element comparison

The Android Back action pops the in-app navigation stack on every secondary interface. Journey Into Matter is intentionally excluded from this version.

## Scientific data and modeling boundaries

The 118-element property catalog is generated from the official NIH PubChem Periodic Table PUG REST dataset:

`https://pubchem.ncbi.nlm.nih.gov/rest/pug/periodictable/JSON`

Run the checked-in generator with:

```bash
node tools/generate_pubchem_catalog.js pubchem-periodic-table.json \
  app/src/main/java/com/richardtalcott/periodic/ScientificCatalog.kt
```

Missing or unestablished source values remain identified as such. Atom and molecule graphics are educational models: nuclei are enlarged for visibility, oval paths represent principal energy levels rather than literal planetary electron trajectories, and the app labels models as illustrative/not to scale. Isotope abundance or stability is only asserted for the evaluated nuclides included in the app. One-atmosphere exceptions such as helium, carbon, and arsenic are handled explicitly in the phase lab.

## Build and validation

The v4 branch workflow runs a clean debug build, unit tests, and Android lint, then uploads the APK artifact:

```bash
./gradlew --no-daemon clean assembleDebug testDebugUnitTest lintDebug
```

APK output:

`app/build/outputs/apk/debug/app-debug.apk`

The project targets Android SDK 35, JDK 17, Jetpack Compose, and SceneView/Filament.
