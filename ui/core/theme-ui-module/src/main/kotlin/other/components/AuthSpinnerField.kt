package other.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Поле выбора из списка. Открывается кликом по всему полю.
 *
 * @param label готовая подпись поля («Пол»). Если не задана — используется «Выберите [labelHint]».
 */
@Composable
fun AuthSpinnerField(
    text: String = "",
    labelHint: String,
    onValueChange: (Any) -> Unit,
    isError: Boolean = false,
    list: List<Any>,
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String? = null,
    leadingIcon: ImageVector? = null
) {
    var value by remember { mutableStateOf(text) }
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            colors = formTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = label ?: "Выберите $labelHint", style = MaterialTheme.typography.bodyMedium)
            },
            leadingIcon = leadingIcon?.let { icon ->
                { Icon(imageVector = icon, contentDescription = null) }
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = "Выпадающий список",
                    modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            isError = isError
        )

        // Прозрачный слой поверх поля: открывает список по клику в любом месте поля
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { isExpanded = true }
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            shape = MaterialTheme.shapes.medium,
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            list.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item.toString(), style = MaterialTheme.typography.bodyMedium)
                    },
                    onClick = {
                        value = item.toString()
                        onValueChange(item)
                        isExpanded = false
                    }
                )
            }
        }
    }
}
