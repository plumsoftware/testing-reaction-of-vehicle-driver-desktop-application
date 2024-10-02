package about_app

import about_app.store.Effect
import about_app.store.Event
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.BackButton
import other.extension.padding.ExtensionPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppPage(navigator: Navigator) {

    val aboutPageViewModel = viewModel(modelClass = AboutAppViewModel::class) {
        AboutAppViewModel()
    }
    val state = aboutPageViewModel.state.collectAsState().value

    LaunchedEffect(key1 = Unit) {
        aboutPageViewModel.effect.collect { effect ->
            when (effect) {
                Effect.BackClick -> {
                    navigator.goBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            BackButton(
                onClick = { aboutPageViewModel.onEvent(Event.BackClick) }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(all = ExtensionPadding.largePadding).padding(it),
            verticalArrangement = Arrangement.spacedBy(
                space = ExtensionPadding.largeVerPadding,
                alignment = Alignment.Top
            ),
        ) {
            Row(
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
            ) {
                Text(text = "Название программы", style = MaterialTheme.typography.bodyMedium)
                TextField(value = state.name, onValueChange = {}, readOnly = true)
            }
            Row(
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
            ) {
                Text(text = "Версия программы", style = MaterialTheme.typography.bodyMedium)
                TextField(value = state.version, onValueChange = {}, readOnly = true)
            }
            Row(
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
            ) {
                Text(
                    text = "Уникальный номер программы",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextField(value = state.number, onValueChange = {}, readOnly = true)
            }
        }
    }
}