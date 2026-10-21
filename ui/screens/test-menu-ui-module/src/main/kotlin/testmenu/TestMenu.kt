package testmenu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.model.dto.test.TestDTO
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.BackButton
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize
import testmenu.components.TestStartDialog
import testmenu.store.Effect
import testmenu.store.Event
import testmenu.viewmodel.TestMenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMenu(
    navigator: Navigator,
    testDto: TestDTO,
    block: (TestDTO) -> Unit = {}
) {

    val testMenuViewModel = viewModel(modelClass = TestMenuViewModel::class) {
        TestMenuViewModel()
    }

    val state = testMenuViewModel.state.collectAsState().value

    LaunchedEffect(key1 = Unit) {
        testMenuViewModel.effect.collect { effect ->
            when (effect) {
                Effect.BackClicked -> {
                    navigator.goBack()
                }

                is Effect.TestClicked -> {
                    block.invoke(effect.testDTO)
                    navigator.navigate(route = effect.route)
                }
            }
        }
    }

    state.selectedTest?.let { test ->
        TestStartDialog(
            test = test,
            step = state.step,
            count = state.count,
            interval = state.interval,
            inputDevice = state.inputDevice,
            onCountChange = { testMenuViewModel.onEvent(Event.CountChanged(it)) },
            onIntervalChange = { testMenuViewModel.onEvent(Event.IntervalChanged(it)) },
            onInputDeviceChange = { testMenuViewModel.onEvent(Event.InputDeviceChanged(it)) },
            onParametersConfirmed = { testMenuViewModel.onEvent(Event.ParametersConfirmed) },
            onBackToParameters = { testMenuViewModel.onEvent(Event.BackToParameters) },
            onStart = { withPractice ->
                testMenuViewModel.onEvent(Event.StartClicked(testDTO = testDto, withPractice = withPractice))
            },
            onDismiss = { testMenuViewModel.onEvent(Event.DialogDismissed) }
        )
    }

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
            ) {
                BackButton(
                    onClick = { testMenuViewModel.onEvent(Event.BackCLicked) }
                )
                Text(
                    text = "Идентификатор пользователя ${testDto.user.id}",
                    style = MaterialTheme.typography.bodySmall
                        .copy(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ширина колонки — по самой длинной кнопке, остальные растягиваются до неё
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                verticalArrangement = ExtensionPadding.mediumVerticalArrangement
            ) {
                for (i in state.reactionTests) {
                    Button(
                        onClick = { testMenuViewModel.onEvent(Event.TestClicked(test = i, testDTO = testDto)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minWidth = ConstantSize.homeButtonWidth)
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = ExtensionPadding.mediumAsymmetricalContentPadding
                    ) {
                        Text(text = "Тест '${i.name}'", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}