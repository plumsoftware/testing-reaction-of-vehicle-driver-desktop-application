package presentation.other.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import domain.model.regular.user.DrivingLicenseCategory
import domain.model.regular.user.Interval

@Composable
fun AuthSpinnerField(
    text: String = "",
    labelHint: String,
    onValueChange: (Any) -> Unit,
    isError: Boolean = false,
    list: List<Any>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    var value by remember { mutableStateOf(text) }
    var isExpanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        enabled = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.Transparent,
            ),
        onValueChange = {},
        modifier = modifier,
        label = {
            Text(text = "Выберите $labelHint", style = MaterialTheme.typography.bodySmall)
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        shape = MaterialTheme.shapes.medium,
        isError = isError,
        trailingIcon = {

            if (isExpanded) {
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = {
                        isExpanded = !isExpanded
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                )
                {
                    list.forEachIndexed { _, any ->

                        val item = when (any) {
                            is DrivingLicenseCategory -> any
                            is Int -> any
                            is Interval -> any
                            else -> DrivingLicenseCategory.Empty
                        }

                        DropdownMenuItem(
                            onClick = {
                                value = item.toString()
                                onValueChange(item)
                                isExpanded = !isExpanded
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        ) {
                            Text(text = item.toString(), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            IconButton(
                onClick = {
                    isExpanded = !isExpanded
                }
            ) {
                Icon(Icons.Rounded.ArrowDropDown, contentDescription = "Выподающий список с категориями прав")
            }
        }
    )
}