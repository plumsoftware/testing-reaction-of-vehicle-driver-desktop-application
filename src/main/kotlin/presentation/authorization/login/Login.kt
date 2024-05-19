package presentation.authorization.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.Constants
import domain.model.regular.DrivingLicenseCategory
import domain.model.regular.Interval
import presentation.authorization.login.store.Event
import presentation.authorization.login.store.State
import presentation.other.components.AuthSpinnerField
import presentation.other.components.AuthTextField
import presentation.other.components.BackButton
import presentation.other.components.DefaultButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.other.extension.padding.ExtensionPadding.mediumHorizontalArrangementCenter
import presentation.other.extension.padding.ExtensionPadding.smallHorizontalArrangementCenter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    onEvent: (Event) -> Unit,
    state: androidx.compose.runtime.State<State>,
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
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding)
                .padding(paddingValues = it),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthTextField(
                labelHint = "Логин",
                onValueChange = {
                    onEvent(Event.OnLoginChanged(login = it))
                },
                isError = state.value.isLoginError
            )

            AuthTextField(
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
                AuthSpinnerField(
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
                    labelHint = "Количество попыток",
                    onValueChange = {
                        it as Int
                        onEvent(Event.OnCountChanged(it))
                    },
                    isError = state.value.isCountError,
                    list = Constants.Test.counts.toList(),
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
            }

            AuthSpinnerField(
                labelHint = "Интервал сигнала в секундах",
                onValueChange = {
                    onEvent(Event.OnIntervalChanged(it as Interval))
                },
                isError = state.value.isIntervalError,
                list = Constants.Test.intervals.toList(),
                modifier = Modifier.wrapContentWidth().wrapContentHeight().align(Alignment.Start)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = smallHorizontalArrangementCenter,
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
        }
    }
}