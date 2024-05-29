package presentation.testmenu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.model.regular.tests.ReactionTest
import domain.model.regular.tests.TestMode
import presentation.other.components.BackButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.other.extension.size.ConstantSize
import presentation.testmenu.store.Event
import presentation.testmenu.viewmodel.TestMenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMenu(
    onEvent: (Event) -> Unit,
    reactionTests: List<ReactionTest>,
    testMenuViewModel: TestMenuViewModel
) {

    val state = testMenuViewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
            ) {
                BackButton(
                    onClick = { onEvent(Event.BackCLicked) }
                )
                Text(
                    text = "Идентификатор пользователя ${state.user.id}",
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
            for (i in reactionTests) {
                Button(
                    onClick = {
                        onEvent(
                            Event.TestClicked(
                                route = i.route,
                                testMode = TestMode(name = i.name, id = i.id.toLong())
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