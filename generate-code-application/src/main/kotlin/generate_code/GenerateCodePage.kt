package generate_code

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import generate_code.store.Event
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.DefaultButton
import other.extension.padding.ExtensionPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateCodePage() {
    val generateCodeViewModel = viewModel(modelClass = GenerateCodeViewModel::class) {
        GenerateCodeViewModel()
    }
    val state = generateCodeViewModel.state.collectAsState()

    Scaffold {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = ExtensionPadding.smallVerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(all = 44.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = ExtensionPadding.largeHorPadding)
            ) {
                OutlinedTextField(
                    modifier = Modifier,
                    value = state.value.keySize.toString(),
                    onValueChange = {
                        generateCodeViewModel.onEvent(Event.ChangeKeySize(keySize = it))
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.Transparent,
                    ),
                    label = {
                        Text(
                            text = "Размер ключа (бит)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    shape = MaterialTheme.shapes.medium,
                    isError = state.value.isError
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1.0f),
                    value = state.value.code ?: "",
                    onValueChange = {},
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.Transparent,
                    ),
                    label = {
                        Text(text = "Ключ", style = MaterialTheme.typography.bodySmall)
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    shape = MaterialTheme.shapes.medium,
                    isError = state.value.isError
                )
            }
            DefaultButton(
                content = {
                    Text(
                        text = "Сгенерировать можно только раз за сессию",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    generateCodeViewModel.onEvent(Event.GenerateCode)
                },
                enabled = state.value.enabled
            )
        }
    }
}