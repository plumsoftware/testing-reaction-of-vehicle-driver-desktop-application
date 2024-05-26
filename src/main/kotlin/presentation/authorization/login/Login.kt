package presentation.authorization.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.Constants
import domain.model.regular.user.DrivingLicenseCategory
import domain.model.regular.user.Interval
import presentation.authorization.login.store.Event
import presentation.authorization.login.store.State
import presentation.other.components.AuthSpinnerField
import presentation.other.components.AuthTextField
import presentation.other.components.BackButton
import presentation.other.components.DefaultButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.other.extension.padding.ExtensionPadding.mediumHorizontalArrangementCenter

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
                content = { Text(text = "Перейти в меню с тестами", style = MaterialTheme.typography.headlineMedium) })
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
                    list = Constants.Test.counts.toList(),
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
                list = Constants.Test.intervals.toList(),
                modifier = Modifier.wrapContentWidth().wrapContentHeight().align(Alignment.Start)
            )
        }
    }
}