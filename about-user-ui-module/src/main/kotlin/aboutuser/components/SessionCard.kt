package aboutuser.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import data.model.dto.database.SessionDTO
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize
import theme.ExtendedTheme
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

    var isExpanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

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
            IconButton(
                onClick = {
                    isExpanded = !isExpanded
                }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = "Показать дополнительную информацию",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
        if (isExpanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement,
                modifier = Modifier.wrapContentWidth().wrapContentHeight()
            ) {
                Text(
                    text = "Категория водительского удостоверения ${sessionDTO.drivingLicenseCategory}.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                )
            }
            Text(
                text = "Стаж вождения на момент прохождения тестирования ${sessionDTO.experience} ${getEnd(sessionDTO)}.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .background(
                    color = Color.Transparent,
                    shape = MaterialTheme.shapes.small
                ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
            thickness = ConstantSize.dividerStatisticHeight
        )
    }
}

private fun getEnd(sessionDTO: SessionDTO): String {
    val end = if (sessionDTO.experience.toInt() > 4) {
        "лет"
    } else {
        if (sessionDTO.experience.toInt() == 0) {
            "лет"
        } else {
            if (sessionDTO.experience.toInt() == 1) {
                "год"
            } else {
                "года"
            }
        }
    }
    return end
}