package presentation.aboutuser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import presentation.aboutuser.store.Event
import presentation.aboutuser.viewmodel.AboutUserViewModel
import presentation.aboutuser.viewmodel.getname
import presentation.other.components.BackButton
import presentation.other.components.DefaultButton
import presentation.other.extension.padding.ExtensionPadding


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUserPage(onEvent: (Event) -> Unit, aboutUserViewModel: AboutUserViewModel) {
    val state = aboutUserViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            BackButton(
                onClick = { onEvent(Event.BackButtonClicked) }
            )
        },
        floatingActionButton = {
            DefaultButton(
                onClick = { onEvent(Event.SaveChanges) },
                content = { Text(text = "Сохранить изменения", style = MaterialTheme.typography.headlineMedium) })
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getname(
                    name = state.value.user.name,
                    surname = state.value.user.surname,
                    patronymic = state.value.user.patronymic
                ),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Black
            )
        }
    }
}