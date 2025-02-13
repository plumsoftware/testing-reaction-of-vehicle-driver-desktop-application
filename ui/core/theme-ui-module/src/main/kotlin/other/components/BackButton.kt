package other.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")//TODO: Replace with res id
    }
}