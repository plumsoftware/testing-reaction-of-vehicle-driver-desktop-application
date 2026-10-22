package other.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.nativeKeyLocation
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import data.model.regular.settings.Pedal
import data.model.regular.settings.PedalBindings
import data.model.regular.settings.PedalKey
import java.awt.event.KeyEvent as AwtKeyEvent

/**
 * Карточка настроек «Педали»: назначение клавиш USB-педалям, которые при нажатии посылают
 * нажатие клавиши (как клавиатура). Работает одинаково на Windows, macOS и Linux.
 */
@Composable
fun PedalSettingsCard(pedals: PedalBindings, onChange: (PedalBindings) -> Unit) {
    var capturing by remember { mutableStateOf<Pedal?>(null) }
    var checking by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Педали", style = MaterialTheme.typography.headlineSmall)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Для USB-педалей, которые при нажатии посылают клавишу, как клавиатура. " +
                        "Нажмите «Назначить» и нажмите педаль. После этого в окне перед тестом можно выбрать «Педали».",
                    style = MaterialTheme.typography.bodyMedium
                )
                Pedal.entries.forEach { pedal ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = pedal.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.width(96.dp)
                        )
                        Text(
                            text = pedals[pedal]?.label ?: "не назначена",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (pedals[pedal] == null) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.width(160.dp)
                        )
                        OutlinedButton(onClick = { capturing = pedal }) {
                            Text(text = "Назначить", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (pedals[pedal] != null) {
                            TextButton(onClick = { onChange(pedals.with(pedal, null)) }) {
                                Text(text = "Сбросить", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                if (Pedal.entries.any { pedals[it] != null }) {
                    Button(onClick = { checking = true }) {
                        Text(text = "Проверить педали", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    capturing?.let { pedal ->
        PedalCaptureDialog(
            pedal = pedal,
            validate = { key ->
                Pedal.entries.firstOrNull { other -> other != pedal && pedals[other]?.keyCode == key.keyCode }
                    ?.let { other -> "Эта клавиша уже назначена на педаль «$other». Нажмите другую педаль." }
            },
            onCaptured = { key ->
                onChange(pedals.with(pedal, key))
                capturing = null
            },
            onDismiss = { capturing = null }
        )
    }

    if (checking) {
        PedalCheckDialog(pedals = pedals, onDismiss = { checking = false })
    }
}

/** Ждёт нажатия педали и возвращает её клавишу; [validate] возвращает текст ошибки или null. */
@Composable
private fun PedalCaptureDialog(
    pedal: Pedal,
    validate: (PedalKey) -> String?,
    onCaptured: (PedalKey) -> Unit,
    onDismiss: () -> Unit,
) {
    var error by remember { mutableStateOf<String?>(null) }
    KeyListeningDialog(
        title = "Педаль «$pedal»",
        onDismiss = onDismiss,
        onKeyDown = { event ->
            val key = event.toPedalKey()
            error = when {
                key == null -> "Программа не может различить эту клавишу. " +
                    "Настройте педаль на обычную клавишу (буква, цифра, F1–F12) в программе производителя."

                else -> validate(key)
            }
            if (error == null && key != null) onCaptured(key)
        }
    ) {
        Text(text = "Нажмите педаль «${pedal.toString().lowercase()}».", style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "Подойдёт и клавиша на клавиатуре, если педали нет под рукой.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        error?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error) }
    }
}

/** Показывает, какая педаль сейчас нажата, — чтобы проверить назначение перед тестами. */
@Composable
private fun PedalCheckDialog(pedals: PedalBindings, onDismiss: () -> Unit) {
    val pressed = remember { mutableStateListOf<Long>() }
    KeyListeningDialog(
        title = "Проверка педалей",
        onDismiss = onDismiss,
        onKeyDown = { event -> pressed.add(event.key.keyCode) },
        onKeyUp = { event -> pressed.remove(event.key.keyCode) }
    ) {
        Text(text = "Нажимайте педали: нажатая подсвечивается.", style = MaterialTheme.typography.bodyLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Pedal.entries.forEach { pedal ->
                val key = pedals[pedal]
                val isPressed = key != null && key.keyCode in pressed
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = when {
                        isPressed -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceContainerHighest
                    },
                    contentColor = if (isPressed) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                ) {
                    Column(
                        modifier = Modifier.width(160.dp).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = pedal.toString(), style = MaterialTheme.typography.titleMedium)
                        Text(text = key?.label ?: "не назначена", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/**
 * Окно, которое держит фокус и перехватывает все клавиши (в том числе Esc и Пробел),
 * чтобы педаль можно было назначить на любую клавишу. Закрывается только кнопкой.
 * Автоповтор удерживаемой клавиши не передаётся.
 */
@Composable
private fun KeyListeningDialog(
    title: String,
    onDismiss: () -> Unit,
    onKeyDown: (KeyEvent) -> Unit,
    onKeyUp: (KeyEvent) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val held = remember { mutableStateListOf<Key>() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.widthIn(max = 560.dp)
        ) {
            Box(
                modifier = Modifier
                    .onPreviewKeyEvent { event ->
                        when (event.type) {
                            KeyEventType.KeyDown -> {
                                if (event.key !in held) {
                                    held.add(event.key)
                                    onKeyDown(event)
                                }
                            }

                            KeyEventType.KeyUp -> {
                                held.remove(event.key)
                                onKeyUp(event)
                            }
                        }
                        true
                    }
                    .focusRequester(focusRequester)
                    .focusable()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = title, style = MaterialTheme.typography.headlineSmall)
                    content()
                    TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                        Text(text = "Закрыть", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

/** Клавиша педали с названием для экрана; null — клавиша без кода, её не отличить от других. */
private fun KeyEvent.toPedalKey(): PedalKey? {
    val code = key.nativeKeyCode
    if (code == AwtKeyEvent.VK_UNDEFINED) return null
    val side = when (key.nativeKeyLocation) {
        AwtKeyEvent.KEY_LOCATION_LEFT -> " (левая)"
        AwtKeyEvent.KEY_LOCATION_RIGHT -> " (правая)"
        AwtKeyEvent.KEY_LOCATION_NUMPAD -> " (цифровой блок)"
        else -> ""
    }
    return PedalKey(keyCode = key.keyCode, label = AwtKeyEvent.getKeyText(code) + side)
}
