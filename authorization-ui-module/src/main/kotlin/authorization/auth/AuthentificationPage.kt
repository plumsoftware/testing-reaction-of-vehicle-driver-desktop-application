package authorization.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import authorization.auth.store.Effect
import other.components.BackButton
import other.extension.padding.ExtensionPadding
import authorization.auth.store.Event
import authorization.auth.viewmodel.AuthorizationViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.AuthTextField
import other.components.DefaultButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorizationPage(navigator: Navigator) {

    val authorizationViewModel: AuthorizationViewModel =
        viewModel(modelClass = AuthorizationViewModel::class) {
            AuthorizationViewModel()
        }


    LaunchedEffect(key1 = Unit) {
        authorizationViewModel.effect.collect { effect ->
            when (effect) {
                Effect.GoBack -> navigator.goBack()
            }
        }
    }


    Scaffold(
        topBar = {
            BackButton(
                onClick = { authorizationViewModel.onEvent(Event.BackCLicked) }
            )
        },
        floatingActionButton = {
            DefaultButton(
                onClick = { authorizationViewModel.onEvent(Event.StartTest) },
                content = {
                    Text(
                        text = "Пройти тест",
                        style = MaterialTheme.typography.headlineMedium
                    )
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthTextField(labelHint = "Фамилию", onValueChange = {
                authorizationViewModel.onEvent(Event.OnNameChanged(name = it))
            })
            AuthTextField(labelHint = "Имя", onValueChange = {
                authorizationViewModel.onEvent(Event.OnSurnameChanged(surname = it))
            })
            AuthTextField(labelHint = "Отчество (при наличии)", onValueChange = {
                authorizationViewModel.onEvent(Event.OnPatronymicChanged(patronymic = it))
            })
        }
    }
}