package presentation.authorization.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.constant.TestConstants
import data.model.regular.user.DrivingLicenseCategory
import data.model.regular.user.Interval
import other.components.AuthSpinnerField
import other.components.AuthTextField
import other.components.BackButton
import other.components.DefaultButton
import other.extension.padding.ExtensionPadding
import other.extension.padding.ExtensionPadding.mediumHorizontalArrangementCenter
import presentation.authorization.login.store.Event
import presentation.authorization.login.store.State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    onEvent: (Event) -> Unit,
    state: androidx.compose.runtime.State<State>
) {
    Scaffold(
        topBar = {
            BackButton(
                onClick = { onEvent(Event.BackClicked) }
            )
        },
        floatingActionButton = {
            DefaultButton(
                onClick = { onEvent(Event.StartTest) },
                content = { Text(text = "Перейти в меню с тестами", style = MaterialTheme.typography.headlineMedium) },
                enabled = state.value.isEnableStartTest
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it)
                .padding(paddingValues = ExtensionPadding.mediumSymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthTextField(
                text = state.value.login,
                labelHint = "Логин",
                onValueChange = {
                    onEvent(Event.OnLoginChanged(login = it))
                },
                isError = state.value.isLoginError
            )

            AuthTextField(
                text = state.value.password,
                labelHint = "Пароль",
                onValueChange = {
                    onEvent(Event.OnPasswordChanged(password = it))
                },
                isError = state.value.isPasswordError
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = mediumHorizontalArrangementCenter,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                AuthTextField(
                    text = if (state.value.experience == 0) "" else state.value.experience.toString(),
                    labelHint = "Стаж вождения",
                    onValueChange = {
                        onEvent(
                            Event.OnExperienceChanged(
                                experience = try {
                                    it.toInt()
                                } catch (e: Exception) {
                                    0
                                }
                            )
                        )
                    },
                    isError = state.value.isExperienceError,
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                AuthTextField(
                    text = if (state.value.age < 0) "" else state.value.age.toString(),
                    labelHint = "Возраст",
                    onValueChange = {
                        onEvent(
                            Event.OnAgeChanged(
                                age = try {
                                    it.toInt()
                                } catch (e: Exception) {
                                    -1
                                }
                            )
                        )
                    },
                    isError = state.value.isAgeError,
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                AuthSpinnerField(
                    text = if (state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty) "" else state.value.drivingLicenseCategory.toString(),
                    labelHint = "Категория прав",
                    onValueChange = {
                        it as DrivingLicenseCategory
                        onEvent(Event.OnDrivingLicenseCategoryChanged(it))
                    },
                    isError = state.value.isDrivingLicenseCategoryError,
                    list = DrivingLicenseCategory.entries,
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                AuthSpinnerField(
                    text = if (state.value.count == 0) "" else state.value.count.toString(),
                    labelHint = "Количество попыток",
                    onValueChange = {
                        it as Int
                        onEvent(Event.OnCountChanged(it))
                    },
                    isError = state.value.isCountError,
                    list = TestConstants.counts.toList(),
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
            }

            AuthSpinnerField(
                text = if (state.value.selectedInterval == Interval()) "" else state.value.selectedInterval.toString(),
                labelHint = "Интервал сигнала в секундах",
                onValueChange = {
                    onEvent(Event.OnIntervalChanged(it as Interval))
                },
                isError = state.value.isIntervalError,
                list = TestConstants.Tests.intervals.toList(),
                modifier = Modifier.wrapContentWidth().wrapContentHeight().align(Alignment.Start)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = ExtensionPadding.smallHorizontalArrangementCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = "Ознакомьтесь и подтвердите",
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(
                        shape = MaterialTheme.shapes.medium,
                        onClick = {
                            onEvent(Event.OpenPrivacyPolicy)
                        }
                    ) {
                        Text(
                            text = "политика конфиденциальности",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Checkbox(
                        checked = state.value.isEnableStartTest,
                        onCheckedChange = {
                            onEvent(Event.OnEnableStartTestChanged(enabled = it))
                        }
                    )
                }
            }

            //Карточка с ошибкой
            AnimatedVisibility(
                visible = state.value.roamingErrorMessage.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
                        text = state.value.roamingErrorMessage,
                        modifier = Modifier.padding(ExtensionPadding.smallPadding),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}