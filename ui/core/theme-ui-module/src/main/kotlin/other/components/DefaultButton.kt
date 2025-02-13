package other.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize

@Composable
fun DefaultButton(content: @Composable() (RowScope.() -> Unit), onClick: () -> Unit, colors: ButtonColors = ButtonDefaults.buttonColors(), enabled: Boolean = true) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .defaultMinSize(minWidth = ConstantSize.homeButtonWidth)
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium, contentPadding = ExtensionPadding.mediumAsymmetricalContentPadding,
        colors = colors,
        content = content,
        enabled = enabled
    )
}