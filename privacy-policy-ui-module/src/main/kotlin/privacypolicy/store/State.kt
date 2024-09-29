package privacypolicy.store

import androidx.compose.ui.text.AnnotatedString
import utils.privacyPolicyFormatter

data class State(
    val privacyPolicyText: AnnotatedString = privacyPolicyFormatter()
)