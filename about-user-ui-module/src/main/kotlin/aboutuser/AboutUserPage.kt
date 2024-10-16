import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import data.constant.UIConstants
import data.model.either.AppEither
import data.model.regular.user.User
import domain.storage.UserStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.AuthTextField
import other.components.BackButton
import other.components.DefaultButton
import other.components.Nothing
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize
import aboutuser.components.LinearChart
import aboutuser.components.SessionCard
import aboutuser.store.Effect
import aboutuser.store.Event
import aboutuser.viewmodel.AboutUserViewModel
import domain.storage.SessionStorage
import theme.ExtendedTheme


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AboutUserPage(navigator: Navigator, userStorage: UserStorage, sessionStorage: SessionStorage, user: User) {

    val aboutUserViewModel: AboutUserViewModel =
        viewModel(modelClass = AboutUserViewModel::class) {
            AboutUserViewModel(
                userStorage = userStorage,
                sessionStorage = sessionStorage,
                user = user
            )
        }

    val state = aboutUserViewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        aboutUserViewModel.effect.collect { effect ->
            when (effect) {
                Effect.BackClicked -> navigator.goBack()
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        aboutUserViewModel.onEvent(Event.InitSessions)
    }

    Scaffold(
        topBar = {
            BackButton(
                onClick = { aboutUserViewModel.onEvent(Event.BackButtonClicked) }
            )
        },
        floatingActionButton = {
            Column(
                modifier = Modifier
                    .wrapContentSize(),
                verticalArrangement = ExtensionPadding.smallVerticalArrangementTop,
                horizontalAlignment = Alignment.Start
            ) {
                DefaultButton(
                    onClick = { aboutUserViewModel.onEvent(Event.SaveChanges) },
                    content = {
                        Text(
                            text = "Сохранить изменения",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )

                DefaultButton(
                    onClick = { aboutUserViewModel.onEvent(Event.DeleteUser) },
                    content = {
                        Text(
                            text = "Удалить пользователя",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { scaffoldContentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            ) {
                                TextField(
                                    value = state.value.user.name,
                                    onValueChange = {
                                        aboutUserViewModel.onEvent(Event.OnNameChanged(name = it))
                                    },
                                    modifier = Modifier.wrapContentSize(),
                                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Black
                                    ),
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.Transparent
                                    )
                                )
                                TextField(
                                    value = state.value.user.surname,
                                    onValueChange = {
                                        aboutUserViewModel.onEvent(Event.OnSurnameChanged(surname = it))
                                    },
                                    modifier = Modifier.wrapContentSize(),
                                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Black
                                    ),
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.Transparent
                                    )
                                )
                                TextField(
                                    value = state.value.user.patronymic,
                                    onValueChange = {
                                        aboutUserViewModel.onEvent(
                                            Event.OnPatronymicChanged(
                                                patronymic = it
                                            )
                                        )
                                    },
                                    modifier = Modifier.wrapContentSize(),
                                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Black
                                    ),
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.Transparent
                                    )
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangementCenter,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            ) {
                                AuthTextField(
                                    text = "",
                                    labelHint = "Логин",
                                    onValueChange = {
                                        aboutUserViewModel.onEvent(Event.OnLoginChanged(login = it))
                                    },
                                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                                    isError = state.value.isLoginError
                                )
                                AuthTextField(
                                    text = "",
                                    labelHint = "Пароль",
                                    onValueChange = {
                                        aboutUserViewModel.onEvent(Event.OnPasswordChanged(password = it))
                                    },
                                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                                    isError = state.value.isPasswordError
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.padding(ExtensionPadding.smallVerPadding))
                    }

                    item {
                        Spacer(modifier = Modifier.padding(ExtensionPadding.smallVerPadding))
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
                                if (state.value.sessions.isNotEmpty())
                                    LinearChart(
                                        list = state.value.sessions.take(30),
                                        acceptedLineParams = state.value.selectedChipList
                                    )
                                else
                                    Nothing()
                            }
                            FlowRow(
                                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                    .padding(ExtensionPadding.mediumAsymmetricalContentPadding)
                                    .padding(start = ExtensionPadding.largeHorPadding)
                            ) {
                                state.value.selectedChipList.forEachIndexed { index, _ ->
                                    var selected by remember { mutableStateOf(index == 0) }
                                    FilterChip(
                                        selected = selected,
                                        onClick = {
                                            selected = !selected
                                            aboutUserViewModel.onEvent(
                                                Event.OnFilterChipClick(
                                                    index = index,
                                                    selected = selected
                                                )
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = UIConstants.lineChartParams[index],
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
                                        shape = MaterialTheme.shapes.medium
                                    )
                                }
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
                                textStyle = MaterialTheme.typography.bodySmall,
                                shape = MaterialTheme.shapes.medium,
                                maxLines = 1,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = Color.Transparent,
                                ),
                                isError = state.value.isFilterError,
                                value = state.value.testNumberFilter,
                                onValueChange = {
                                    aboutUserViewModel.onEvent(Event.OnFilterChange(it))
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            aboutUserViewModel.onEvent(Event.FilterSessions)
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
                    item {
                        Spacer(modifier = Modifier.padding(ExtensionPadding.smallVerPadding))
                    }
                    with(state.value.filteredSessionsList) {
                        if (this.isNotEmpty()) {
                            itemsIndexed(this) { _, item ->
                                SessionCard(item)
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .height(ConstantSize.emptySessionsListHeight)
                                ) {
                                    Image(
                                        painter = painterResource("nothing_to_show.png"),
                                        contentDescription = "Изображение - Ничего не найдено по запросу на фильтрацию тестов",
                                        modifier = Modifier.size(ConstantSize.emptySessionsListSize)
                                            .align(Alignment.Center)
                                    )
                                }
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

            when (val result = state.value.appEither) {
                is AppEither.Exception<*> -> {
                    AnimatedVisibility(
                        visible = state.value.appEither == AppEither.Exception(result.e),
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            modifier = Modifier
                                .wrapContentWidth()
                        ) {
                            Text(
                                text = result.e.toString(),
                                modifier = Modifier.padding(ExtensionPadding.smallPadding),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                AppEither.Handle -> {}
                is AppEither.Success<*> -> {
                    AnimatedVisibility(
                        visible = result == AppEither.Success(result.e),
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = ExtendedTheme.colors.successContainer,
                                contentColor = ExtendedTheme.colors.onSuccessContainer
                            ),
                            modifier = Modifier
                                .wrapContentWidth()
                        ) {
                            Text(
                                text = result.e.toString(),
                                modifier = Modifier.padding(ExtensionPadding.smallPadding),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}