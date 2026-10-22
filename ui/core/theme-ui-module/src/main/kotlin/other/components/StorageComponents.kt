package other.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.io.File

/**
 * Базу данных не удалось открыть (нет ключа шифрования, недоступен сетевой диск).
 * Окно не закрывается само: без базы программа работать не может.
 *
 * @param appFolder папка, куда класть config.json; null — кнопка открытия папки не показывается.
 * @param onOpenSettings null — кнопки настроек нет.
 */
@Composable
fun StorageErrorDialog(
    message: String,
    onRetry: () -> Unit,
    appFolder: File? = null,
    onOpenSettings: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = {},
        modifier = Modifier.widthIn(max = 640.dp),
        title = { Text(text = "Не удалось открыть базу данных", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
                if (appFolder != null) {
                    OutlinedButton(onClick = { openFolder(appFolder) }) {
                        Text(text = "Открыть папку для config.json", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onRetry) { Text(text = "Повторить", style = MaterialTheme.typography.bodyMedium) }
        },
        dismissButton = onOpenSettings?.let {
            {
                TextButton(onClick = it) { Text(text = "Настройки", style = MaterialTheme.typography.bodyMedium) }
            }
        }
    )
}

/**
 * Карточка настроек «Ключ шифрования»: экспорт config.json для других компьютеров и резервной копии.
 *
 * @param resultMessage итог последнего экспорта; пусто — не показывается.
 */
@Composable
fun KeyExportCard(description: String, resultMessage: String, onExportClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Ключ шифрования", style = MaterialTheme.typography.headlineSmall)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onExportClick) {
                    Text(text = "Экспорт ключа (config.json)", style = MaterialTheme.typography.bodyMedium)
                }
                if (resultMessage.isNotEmpty()) {
                    Text(
                        text = resultMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/** Создаёт папку, если её нет, и открывает в Проводнике / Finder. */
private fun openFolder(folder: File) {
    runCatching {
        folder.mkdirs()
        if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(folder)
    }.onFailure { it.printStackTrace() }
}
