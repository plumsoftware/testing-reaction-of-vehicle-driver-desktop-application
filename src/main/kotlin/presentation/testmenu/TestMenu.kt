package presentation.testmenu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.model.ReactionTest
import presentation.components.BackButton
import presentation.extension.padding.ExtensionPadding
import presentation.extension.size.ConstantSize
import presentation.testmenu.store.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMenu(onEvent: (Event) -> Unit, reactionTests: List<ReactionTest>) {
    Scaffold(
        topBar = {
            BackButton(
                onClick = { onEvent(Event.BackCLicked) }
            )
        },
        modifier = Modifier.fillMaxSize().padding(ExtensionPadding.mediumAsymmetricalContentPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in reactionTests) {
                Button(
                    onClick = { onEvent(Event.TestClicked(route = i.route)) },
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