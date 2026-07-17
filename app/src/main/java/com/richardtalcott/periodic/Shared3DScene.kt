package com.richardtalcott.periodic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.sceneview.SceneView
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.math.Color
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Size

/**
 * One real Filament scene shared by the eight approved interfaces.
 * UI remains Compose; chamber geometry, depth, PBR lighting and camera movement are true 3D.
 */
@Composable
fun LaboratoryScene(mode: Int) {
    val engine = rememberEngine()
    val materials = rememberMaterialLoader(engine)
    val blackMetal = remember(materials) {
        materials.createColorInstance(Color(0.012f, 0.035f, 0.055f, 1f), metallic = 0.92f, roughness = 0.22f)
    }
    val blueMetal = remember(materials) {
        materials.createColorInstance(Color(0.015f, 0.22f, 0.36f, 1f), metallic = 0.72f, roughness = 0.18f)
    }
    val cyanGlass = remember(materials) {
        materials.createColorInstance(Color(0.04f, 0.72f, 1.0f, 1f), metallic = 0.18f, roughness = 0.12f)
    }
    val proton = remember(materials) {
        materials.createColorInstance(Color(1.0f, 0.035f, 0.20f, 1f), metallic = 0.28f, roughness = 0.2f)
    }
    val neutron = remember(materials) {
        materials.createColorInstance(Color(0.08f, 0.28f, 0.95f, 1f), metallic = 0.3f, roughness = 0.2f)
    }
    val warm = remember(materials) {
        materials.createColorInstance(Color(1.0f, 0.40f, 0.035f, 1f), metallic = 0.55f, roughness = 0.2f)
    }

    SceneView(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        materialLoader = materials,
        isOpaque = true,
        mainLightNode = rememberMainLightNode(engine) { intensity = 135_000f },
        cameraNode = rememberCameraNode(engine) { position = Position(0f, 1.15f, 8.5f) },
        cameraManipulator = rememberCameraManipulator(),
        autoCenterContent = false,
        autoFitContent = false
    ) {
        // Reflective chamber floor and raised circular exhibit deck.
        CubeNode(size = Size(13f, 0.12f, 9f), position = Position(0f, -2.15f, -1.6f), materialInstance = blackMetal)
        CylinderNode(radius = 3.65f, height = 0.22f, position = Position(0f, -1.88f, -2.0f), materialInstance = blueMetal)
        CylinderNode(radius = 3.18f, height = 0.25f, position = Position(0f, -1.72f, -2.0f), materialInstance = blackMetal)

        // Architectural side pylons and ceiling ribs create parallax when the camera moves.
        listOf(-5.4f, -4.55f, 4.55f, 5.4f).forEachIndexed { index, x ->
            CylinderNode(radius = if (index % 2 == 0) 0.28f else 0.18f, height = 6.2f,
                position = Position(x, 0.3f, -3.1f), materialInstance = if (index % 2 == 0) blueMetal else cyanGlass)
        }
        listOf(-5.8f, -3.9f, -2.0f, 0f, 2.0f, 3.9f, 5.8f).forEach { x ->
            CubeNode(size = Size(0.12f, 5.8f, 0.18f), position = Position(x, 0.45f, -5.1f), materialInstance = blueMetal)
        }
        TorusNode(majorRadius = 5.2f, minorRadius = 0.08f, position = Position(0f, 3.0f, -3.3f), rotation = Rotation(90f, 0f, 0f), materialInstance = cyanGlass)
        TorusNode(majorRadius = 4.35f, minorRadius = 0.11f, position = Position(0f, 2.65f, -3.5f), rotation = Rotation(90f, 0f, 0f), materialInstance = blueMetal)

        // Page-specific physical exhibit. This changes behind the UI while preserving a common style.
        when (mode) {
            0 -> { // periodic table: a bank of floating physical sample blocks
                repeat(18) { i ->
                    val row = i / 6; val col = i % 6
                    CubeNode(size = Size(0.64f, 0.22f, 0.62f),
                        position = Position((col - 2.5f) * 0.78f, 0.65f - row * 0.62f, -3.25f + row * 0.08f),
                        rotation = Rotation(-8f, (col - 2.5f) * 2.5f, 0f), materialInstance = if ((i + row) % 3 == 0) cyanGlass else blueMetal)
                }
            }
            5 -> { // bond builder: physical bent molecule
                SphereNode(radius = 0.62f, position = Position(0f, 0.25f, -2.7f), materialInstance = proton)
                SphereNode(radius = 0.38f, position = Position(-1.18f, -0.55f, -2.45f), materialInstance = cyanGlass)
                SphereNode(radius = 0.38f, position = Position(1.18f, -0.55f, -2.45f), materialInstance = cyanGlass)
                CylinderNode(radius = 0.11f, height = 1.32f, position = Position(-0.58f, -0.16f, -2.55f), rotation = Rotation(0f, 0f, -55f), materialInstance = warm)
                CylinderNode(radius = 0.11f, height = 1.32f, position = Position(0.58f, -0.16f, -2.55f), rotation = Rotation(0f, 0f, 55f), materialInstance = warm)
            }
            else -> { // atom, isotope, ion, states, compare and element profile
                repeat(22) { i ->
                    val a = i * 2.39996f; val r = 0.18f * kotlin.math.sqrt(i + 1f)
                    SphereNode(radius = 0.19f, position = Position(kotlin.math.cos(a) * r, kotlin.math.sin(a) * r + 0.15f, -2.8f + ((i % 5) - 2) * 0.09f), materialInstance = if (i % 2 == 0) proton else neutron)
                }
                TorusNode(majorRadius = 1.65f, minorRadius = 0.025f, position = Position(0f, 0.15f, -2.8f), rotation = Rotation(72f, 0f, 12f), materialInstance = cyanGlass)
                TorusNode(majorRadius = 2.1f, minorRadius = 0.022f, position = Position(0f, 0.15f, -2.8f), rotation = Rotation(55f, 35f, -14f), materialInstance = warm)
                TorusNode(majorRadius = 2.55f, minorRadius = 0.02f, position = Position(0f, 0.15f, -2.8f), rotation = Rotation(78f, -25f, 18f), materialInstance = cyanGlass)
            }
        }
    }
}
