package other.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import other.extension.size.ConstantSize

@Composable
fun Nothing() {
    Box(
        modifier = Modifier.fillMaxWidth()
            .height(ConstantSize.emptySessionsListHeight)
    ) {
        Image(
            painter = painterResource("nothing_to_show.png"),
            contentDescription = "Изображение - Ничего не найдено по запросу на статистику",
            modifier = Modifier.size(ConstantSize.emptySessionsListSize)
                .align(Alignment.Center)
        )
    }
}