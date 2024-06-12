package presentation.aboutuser

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import domain.model.regular.user.User
import presentation.aboutuser.components.LinearChart
import presentation.aboutuser.components.SessionCard
import presentation.aboutuser.store.Event
import presentation.aboutuser.viewmodel.AboutUserViewModel
import presentation.aboutuser.viewmodel.getname
import presentation.other.components.AuthTextField
import presentation.other.components.BackButton
import presentation.other.components.DefaultButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.other.extension.size.ConstantSize
import utils.toLineChartData


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
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Spacer(modifier = Modifier.padding(scaffoldContentPadding))
            }

            if (state.value.user != User.empty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = ExtensionPadding.smallVerticalArrangementTop,
                        horizontalAlignment = Alignment.Start
                    ) {
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
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement,
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    ) {
                        Text(
                            text = "Список тестовых сессий",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            textStyle = MaterialTheme.typography.bodyMedium,
                            shape = MaterialTheme.shapes.medium,
                            maxLines = 1,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color.Transparent,
                            ),
                            isError = state.value.isFilterError,
                            value = state.value.testNumberFilter,
                            onValueChange = {
                                onEvent(Event.OnFilterChange(it))
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        onEvent(Event.FilterSessions)
                                    }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = "Иконка поиска по айди теста"
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = "Сортировать по номеру теста",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.wrapContentHeight().wrapContentHeight(),
                                    textAlign = TextAlign.Start,
                                )
                            }
                        )
                    }
                }
                with(state.value.filteredSessionsList) {
                    if (this.isNotEmpty()) {
                        itemsIndexed(this) { _, item ->
                            SessionCard(item)
                        }
                    } else {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(ConstantSize.emptySessionsListHeight)) {
                                Image(
                                    painter = painterResource("nothing_to_show.png"),
                                    contentDescription = "Изображение - Ничего не найдено по запросу на фильтрацию тестов",
                                    modifier = Modifier.size(ConstantSize.emptySessionsListSize).align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = ExtensionPadding.mediumVerticalArrangementTop,
                    ) {
                        Text(
                            text = "Статистика",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement,
                            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                .padding(ExtensionPadding.mediumAsymmetricalContentPadding)
                        ) {
                            LinearChart(
                                list = state.value.sessions.take(30).toLineChartData()
                            )
                        }
                    }
                }
            } else {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter))
                    }
                }
            }
        }
    }
}