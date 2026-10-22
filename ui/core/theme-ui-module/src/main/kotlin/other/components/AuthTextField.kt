package other.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * @param label готовая подпись поля («Фамилия»). Если не задана — используется «Введите [labelHint]».
 * @param isPassword скрывает ввод и добавляет кнопку «Показать»/«Скрыть».
 */
@Composable
fun AuthTextField(
    text: String = "",
    labelHint: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String? = null,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false
) {
    var value by remember { mutableStateOf(text) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        colors = formTextFieldColors(),
        onValueChange = {
            value = it
            onValueChange(value)
        },
        modifier = modifier,
        label = {
            Text(text = label ?: "Введите $labelHint", style = MaterialTheme.typography.bodyMedium)
        },
        leadingIcon = leadingIcon?.let { icon ->
            { Icon(imageVector = icon, contentDescription = null) }
        },
        trailingIcon = if (isPassword) {
            {
                TextButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Text(
                        text = if (isPasswordVisible) "Скрыть" else "Показать",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        isError = isError
    )
}

/** Общие цвета полей формы: светлая подложка и мягкая граница вместо прозрачного фона. */
@Composable
fun formTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
)
