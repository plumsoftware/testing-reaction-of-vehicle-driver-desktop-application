package presentation.other.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AuthTextField(
    text: String = "",
    labelHint: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    var value by remember { mutableStateOf(text) }

    OutlinedTextField(
        value = value,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.Transparent,
        ),
        onValueChange = {
            value = it
            onValueChange(value)
        },
        modifier = modifier,
        label = {
            Text(text = "Введите $labelHint", style = MaterialTheme.typography.bodySmall)
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
        shape = MaterialTheme.shapes.medium,
        isError = isError
    )
}