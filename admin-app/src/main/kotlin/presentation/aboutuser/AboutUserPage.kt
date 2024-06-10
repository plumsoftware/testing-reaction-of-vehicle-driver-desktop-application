package presentation.aboutuser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.regular.user.User
import domain.model.regular.userstatistic.LinearGraphItem
import presentation.aboutuser.components.LineChart
import presentation.aboutuser.store.Event
import presentation.aboutuser.viewmodel.AboutUserViewModel
import presentation.aboutuser.viewmodel.getname
import presentation.other.components.AuthTextField
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
    ) { scaffoldContentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangementTop,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.padding(scaffoldContentPadding))
            }

            if (state.value.user != User.empty()) {
                item {
                    Text(
                        text = getname(
                            name = state.value.user.name,
                            surname = state.value.user.surname,
                            patronymic = state.value.user.patronymic
                        ),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Black
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = ExtensionPadding.mediumHorizontalArrangementCenter,
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    ) {
                        AuthTextField(
                            text = state.value.user.login,
                            labelHint = "Логин",
                            onValueChange = {
                                onEvent(Event.OnLoginChanged(login = it))
                            },
                            modifier = Modifier.fillMaxWidth().weight(1.0f),
                            isError = state.value.isLoginError
                        )
                        AuthTextField(
                            text = state.value.user.password,
                            labelHint = "Пароль",
                            onValueChange = {
                                onEvent(Event.OnPasswordChanged(password = it))
                            },
                            modifier = Modifier.fillMaxWidth().weight(1.0f),
                            isError = state.value.isPasswordError
                        )
                    }
                }
            } else {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter))
                    }
                }
            }
            item {
                LineChart(
                    data = listOf(
                        LinearGraphItem(10, 15.0),
                        LinearGraphItem(15, 20.0),
                        LinearGraphItem(20, 15.0)
                    ),
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}