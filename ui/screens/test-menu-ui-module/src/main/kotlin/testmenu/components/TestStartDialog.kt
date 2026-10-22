package testmenu.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import data.constant.TestConstants
import data.model.dto.test.InputDevice
import data.model.regular.user.Interval
import kotlinx.coroutines.launch
import data.model.regular.settings.PedalBindings
import model.tests.ReactionTest
import model.tests.ResponseInput
import model.tests.unassignedPedals
import other.components.AuthSpinnerField
import testmenu.store.StartStep
import testmenu.viewmodel.TestMenuViewModel

/**
 * Окно перед запуском теста: шаг 1 — устройство ввода (клавиатура, мышь или педали), количество попыток и интервал;
 * шаг 2 — обучение слайдами под выбранное устройство; на последнем слайде можно пропустить тренировку.
 * Для тестов с фиксированной методикой (PVT) попытки и интервал не выбираются.
 */
@Composable
fun TestStartDialog(
    test: ReactionTest,
    step: StartStep,
    count: Int,
    interval: Interval,
    inputDevice: InputDevice,
    pedals: PedalBindings,
    onCountChange: (Int) -> Unit,
    onIntervalChange: (Interval) -> Unit,
    onInputDeviceChange: (InputDevice) -> Unit,
    onParametersConfirmed: () -> Unit,
    onBackToParameters: () -> Unit,
    /** true — с тренировкой, false — сразу зачётная часть. */
    onStart: (withPractice: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.padding(24.dp).widthIn(max = 880.dp).heightIn(max = 680.dp)
        ) {
            Column(modifier = Modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(text = test.name, style = MaterialTheme.typography.headlineMedium)
                when (step) {
                    StartStep.PARAMETERS -> ParametersStep(
                        test = test,
                        count = count,
                        interval = interval,
                        inputDevice = inputDevice,
                        pedals = pedals,
                        onCountChange = onCountChange,
                        onIntervalChange = onIntervalChange,
                        onInputDeviceChange = onInputDeviceChange,
                        onNext = onParametersConfirmed,
                        onDismiss = onDismiss
                    )

                    StartStep.TUTORIAL -> TutorialStep(
                        test = test,
                        input = ResponseInput(device = inputDevice, pedals = pedals),
                        onBack = onBackToParameters,
                        onStart = onStart
                    )
                }
            }
        }
    }
}

@Composable
private fun ParametersStep(
    test: ReactionTest,
    count: Int,
    interval: Interval,
    inputDevice: InputDevice,
    pedals: PedalBindings,
    onCountChange: (Int) -> Unit,
    onIntervalChange: (Interval) -> Unit,
    onInputDeviceChange: (InputDevice) -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column(
            modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = "Чем будете отвечать?", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                TestMenuViewModel.selectableInputDevices(test).forEach { device ->
                    DeviceCard(
                        device = device,
                        test = test,
                        pedals = pedals,
                        selected = device == inputDevice,
                        onClick = { onInputDeviceChange(device) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (!test.hasFixedProtocol) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    AuthSpinnerField(
                        text = count.toString(),
                        labelHint = "количество попыток",
                        label = "Количество попыток",
                        leadingIcon = Icons.Rounded.Refresh,
                        onValueChange = { onCountChange(it as Int) },
                        list = TestConstants.counts.toList(),
                        modifier = Modifier.weight(1.0f)
                    )
                    AuthSpinnerField(
                        text = interval.toString(),
                        labelHint = "интервал сигнала",
                        label = "Интервал до сигнала, с",
                        leadingIcon = Icons.Rounded.Notifications,
                        onValueChange = { onIntervalChange(it as Interval) },
                        list = TestConstants.Tests.intervals.toList(),
                        modifier = Modifier.weight(1.0f)
                    )
                }
                Text(
                    text = "Время до каждого сигнала выбирается случайно в пределах интервала, " +
                        "чтобы нельзя было привыкнуть к ритму.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        // Педали выбраны, но нужная тесту не назначена: тест не на что будет нажимать
        val missingPedals = if (inputDevice == InputDevice.PEDALS) test.unassignedPedals(pedals) else emptyList()
        if (missingPedals.isNotEmpty()) {
            Text(
                text = "Не назначена педаль: ${missingPedals.joinToString { "«$it»" }}. " +
                    "Назначьте её в разделе «Настройки» → «Педали» или выберите другое устройство.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onDismiss) { Text("Отмена", style = MaterialTheme.typography.bodyMedium) }
            Button(onClick = onNext, enabled = missingPedals.isEmpty()) {
                Text("Далее: обучение", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun DeviceCard(
    device: InputDevice,
    test: ReactionTest,
    pedals: PedalBindings,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        border = BorderStroke(
            width = if (selected) 3.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.height(96.dp), contentAlignment = Alignment.Center) {
                when (device) {
                    InputDevice.KEYBOARD -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        test.responses.map { it.keyLabel }.distinct().forEach { KeyCap(label = it, height = 48.dp) }
                    }

                    InputDevice.PEDALS -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        test.responses.mapNotNull { it.pedal }.distinct().forEach { pedal ->
                            KeyCap(label = pedal.toString(), height = 48.dp)
                        }
                    }

                    else -> MouseIllustration()
                }
            }
            Text(text = device.toString(), style = MaterialTheme.typography.titleMedium)
            Text(
                text = when (device) {
                    InputDevice.KEYBOARD -> "Рекомендуется: пальцы лежат на клавишах, рука не двигается"
                    InputDevice.PEDALS -> if (test.unassignedPedals(pedals).isEmpty()) "Нажатие педали ногой, как в автомобиле"
                    else "Сначала назначьте педали: «Настройки» → «Педали»"
                    else -> "Нажатие левой кнопкой по элементу на экране"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TutorialStep(
    test: ReactionTest,
    input: ResponseInput,
    onBack: () -> Unit,
    onStart: (withPractice: Boolean) -> Unit,
) {
    val slides = tutorialSlides(test = test, input = input)
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()
    val page = pagerState.currentPage

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "${page + 1}/${slides.size}. ${slides[page].title}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.weight(1f, fill = false).heightIn(min = 320.dp)
        ) { index ->
            Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                slides[index].content()
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            PageIndicator(count = slides.size, current = page)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    if (page == 0) onBack() else scope.launch { pagerState.animateScrollToPage(page - 1) }
                }
            ) {
                Text(if (page == 0) "К параметрам" else "Назад", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (page == slides.lastIndex) {
                OutlinedButton(onClick = { onStart(false) }) {
                    Text("Пропустить тренировку", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onStart(true) }) {
                    Text("Начать тренировку", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Button(onClick = { scope.launch { pagerState.animateScrollToPage(page + 1) } }) {
                    Text("Далее", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(count: Int, current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == current) 12.dp else 8.dp)
                    .background(
                        color = if (index == current) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
                    .align(Alignment.CenterVertically)
            )
        }
    }
}
