package presentation.privacypolicy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.other.components.BackButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.privacypolicy.store.Event
import utils.privacyPolicyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicy(onEvent: (Event) -> Unit) {
    Scaffold(
        topBar = {
            BackButton(
                onClick = { onEvent(Event.BackCLicked) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = privacyPolicyFormatter(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}