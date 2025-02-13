package privacypolicy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.BackButton
import other.extension.padding.ExtensionPadding
import privacypolicy.store.Event
import privacypolicy.viewmodel.PrivacyPolicyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicy() {

    val privacyPolicyViewModel =
        viewModel(modelClass = PrivacyPolicyViewModel::class) {
            PrivacyPolicyViewModel()
        }
    val state = privacyPolicyViewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            BackButton(
                onClick = { privacyPolicyViewModel.onEvent(Event.BackCLicked) }
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
                    text = state.privacyPolicyText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}