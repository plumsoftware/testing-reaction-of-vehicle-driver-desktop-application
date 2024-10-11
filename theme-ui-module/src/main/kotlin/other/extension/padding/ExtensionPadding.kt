package other.extension.padding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

object ExtensionPadding {
    private val smallItemSpacing = 8.dp
    private val mediumItemSpacing = 18.dp
    private val largeItemSpacing = 32.dp

    private val mediumHorPadding = 22.dp
    private val smallHorPadding = 16.dp
    val largeHorPadding = 26.dp
    val emptySessionsListSize = 22.dp
    private val mediumVerPadding = 18.dp
    val smallVerPadding = 14.dp
    val largeVerPadding = 24.dp

    val largePadding = 26.dp

    val scaffoldTopPadding = 80.dp

    val smallPadding = 16.dp

    val mediumVerticalArrangement =
        Arrangement.spacedBy(space = mediumItemSpacing, alignment = Alignment.CenterVertically)

    val smallVerticalArrangementTop =
        Arrangement.spacedBy(space = smallItemSpacing, alignment = Alignment.Top)
    val smallHorizontalArrangementCenter = Arrangement.Center
    val mediumVerticalArrangementTop =
        Arrangement.spacedBy(space = mediumItemSpacing, alignment = Alignment.Top)
    val largeVerticalArrangementTop =
        Arrangement.spacedBy(space = largeItemSpacing, alignment = Alignment.Top)

    val mediumAsymmetricalContentPadding = PaddingValues(horizontal = mediumHorPadding, vertical = mediumVerPadding)
    val mediumSymmetricalContentPadding = PaddingValues(horizontal = mediumHorPadding)

    val smallAsymmetricalContentPadding = PaddingValues(horizontal = smallHorPadding, vertical = smallVerPadding)
    val smallVerticalContentPadding = PaddingValues(vertical = smallVerPadding)

    val mediumHorizontalArrangement =
        Arrangement.spacedBy(space = mediumItemSpacing, alignment = Alignment.Start)
    val mediumHorizontalArrangementCenter =
        Arrangement.spacedBy(space = mediumItemSpacing, alignment = Alignment.CenterHorizontally)

    object Road {
        val roadMarkerPaddingTop = 70.dp

        val carTopSideViewPadding = 50.dp
    }
}