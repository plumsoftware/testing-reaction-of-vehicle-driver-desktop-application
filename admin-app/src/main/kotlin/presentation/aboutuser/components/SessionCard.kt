package presentation.aboutuser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import domain.model.dto.database.SessionDTO
import presentation.other.extension.padding.ExtensionPadding
import presentation.other.extension.size.ConstantSize
import presentation.theme.ExtendedTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SessionCard(sessionDTO: SessionDTO) {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, sessionDTO.testYear)
    cal.set(Calendar.MONTH, sessionDTO.testMonth)
    cal.set(Calendar.DAY_OF_MONTH, sessionDTO.testDay)
    cal.set(Calendar.HOUR_OF_DAY, sessionDTO.testHourOfDay24h)
    cal.set(Calendar.MINUTE, sessionDTO.testMinuteOfHour)

    Column(
        modifier = Modifier
            .wrapContentWidth(),
        verticalArrangement = ExtensionPadding.mediumVerticalArrangementTop,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement,
            modifier = Modifier.wrapContentWidth().wrapContentHeight()
        ) {
            Text(
                text = "Сессия №${sessionDTO.sessionId} ${
                    SimpleDateFormat(
                        "dd.MM.yyyy hh:mm",
                        Locale.getDefault()
                    ).format(Date(cal.timeInMillis))
                } с интервалом ${sessionDTO.signalInterval} секунд",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            )

            Text(
                text = "№ теста ${sessionDTO.testId}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement,
            modifier = Modifier.wrapContentWidth().wrapContentHeight()
        ) {
            Text(
                text = "Среднее значение ${sessionDTO.averageValue} секунд",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
            )
            Text(
                text = "Стандартное отклонение ${sessionDTO.standardDeviation}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement,
            modifier = Modifier.wrapContentWidth().wrapContentHeight()
        ) {
            Text(
                text = "Попыток ${sessionDTO.count}",
                style = MaterialTheme.typography.bodyMedium.copy(color = ExtendedTheme.colors.onSuccessContainer),
                textAlign = TextAlign.Start,
            )
            Text(
                text = "Ошибок ${sessionDTO.errors}",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error),
                textAlign = TextAlign.Start,
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(ConstantSize.dividerStatisticHeight)
                .background(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.large
                )
                .alpha(alpha = 0.8f)
        )
    }
}