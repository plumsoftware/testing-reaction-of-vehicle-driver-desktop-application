package presentation.testmenu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.model.dto.test.TestDTO
import data.model.regular.tests.TestMode
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.BackButton
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize
import presentation.testmenu.store.Effect
import presentation.testmenu.store.Event
import presentation.testmenu.viewmodel.TestMenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMenu(
    navigator: Navigator,
    testDto: TestDTO,
    block: (TestDTO) -> Unit = {}
) {

    val testMenuViewModel = viewModel(modelClass = TestMenuViewModel::class) {
        TestMenuViewModel(
//            output = { output ->
//                when (output) {
//                    is Output.TestClicked -> {
//                        trafficLightTestViewModel?.onEvent(Event.InitTestMode(testMode = output.testMode))
//                        navigator.navigate(route = output.route)
//                    }
//
//                    Output.BackButtonClicked -> {
//                        navigator.goBack()
//                    }
//                }
//            }
        )
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
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in state.reactionTests) {
                Button(
                    onClick = {
                        val testMode = TestMode(name = i.name, id = i.id.toLong())
                        val testDto_ = testDto.copy(testMode = testMode)

                        testMenuViewModel.onEvent(
                            Event.TestClicked(
                                route = i.route,
                                testDTO = testDto_
                            )
                        )
                    },
                    modifier = Modifier
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