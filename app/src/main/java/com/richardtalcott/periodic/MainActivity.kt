package com.richardtalcott.periodic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            PeriodicTheme {
                PeriodicApp()
            }
        }
    }
}

@Composable
private fun PeriodicApp() {
    val navigation = remember { mutableStateListOf(AppPage.TABLE) }
    var selectedAtomicNumber by remember { mutableIntStateOf(26) }
    var comparisonAtomicNumber by remember { mutableIntStateOf(29) }
    val page = navigation.last()
    val selected = elementByAtomicNumber(selectedAtomicNumber)
    val comparison = elementByAtomicNumber(comparisonAtomicNumber)

    fun navigate(destination: AppPage) {
        if (navigation.lastOrNull() != destination) navigation += destination
    }

    fun goBack() {
        if (navigation.size > 1) navigation.removeAt(navigation.lastIndex)
    }

    BackHandler(enabled = navigation.size > 1, onBack = ::goBack)

    Box(Modifier.fillMaxSize()) {
        LaboratoryScene(page.ordinal)
        LaboratoryOverlay()
        Box(Modifier.fillMaxSize().safeDrawingPadding()) {
            Crossfade(page, animationSpec = tween(260), label = "periodic-navigation") { destination ->
                when (destination) {
                    AppPage.TABLE -> PeriodicTableScreen { element ->
                        selectedAtomicNumber = element.atomicNumber
                        if (comparisonAtomicNumber == selectedAtomicNumber) {
                            comparisonAtomicNumber = if (selectedAtomicNumber == 29) 26 else 29
                        }
                        navigate(AppPage.INFO)
                    }
                    AppPage.INFO -> ElementInformationScreen(selected, ::goBack, ::navigate)
                    AppPage.ATOM -> AtomExplorerScreen(selected, ::goBack)
                    AppPage.ISOTOPE -> IsotopeLabScreen(selected, ::goBack)
                    AppPage.ION -> IonBuilderScreen(selected, ::goBack)
                    AppPage.BOND -> BondBuilderScreen(selected, ::goBack)
                    AppPage.STATES -> StatesOfMatterScreen(selected, ::goBack)
                    AppPage.COMPARE -> CompareElementsScreen(
                        primary = selected,
                        comparison = comparison,
                        onComparisonChanged = { candidate ->
                            if (candidate.atomicNumber != selectedAtomicNumber) comparisonAtomicNumber = candidate.atomicNumber
                        },
                        onSwap = {
                            val oldPrimary = selectedAtomicNumber
                            selectedAtomicNumber = comparisonAtomicNumber
                            comparisonAtomicNumber = oldPrimary
                        },
                        onBack = ::goBack,
                    )
                }
            }
        }
    }
}
