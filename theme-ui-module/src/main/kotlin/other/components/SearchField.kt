package other.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun SearchField(onSearchClick: (String) -> Unit) {
    val text = remember { mutableStateOf("") }
    OutlinedTextField(
        value = text.value,
        onValueChange = {
            text.value = it
        },
        leadingIcon = {
            IconButton(
                onClick = {
                    onSearchClick(text.value)
                }
            ) {
                Icon(imageVector = Icons.Rounded.Search, contentDescription = "Кнопка поиска пользователя")
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    )
}