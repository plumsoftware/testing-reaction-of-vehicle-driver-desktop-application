package testmenu.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import model.tests.PRACTICE_TRIALS
import data.model.dto.test.InputDevice
import model.tests.ReactionTest
import model.tests.ResponseInput

/** Слайд обучения: заголовок, картинка и текст. */
class TutorialSlide(val title: String, val content: @Composable () -> Unit)

/** Слайды обучения перед тестом; второй слайд зависит от устройства ввода. */
fun tutorialSlides(test: ReactionTest, input: ResponseInput): List<TutorialSlide> = listOf(
    TutorialSlide("Что будет на экране") { ScreenSlide(test) },
    TutorialSlide("Как отвечать: ${input.device.toString().lowercase()}") {
        when (input.device) {
            InputDevice.KEYBOARD -> KeyboardSlide(test)
            InputDevice.PEDALS -> PedalsSlide(test)
            else -> MouseSlide(test)
        }
    },
    TutorialSlide("Что считается ошибкой") { SlideText(test.errorsDescription) },
    TutorialSlide("Тренировка и тест") { PracticeSlide(input.device) },
)

@Composable
private fun ScreenSlide(test: ReactionTest) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            test.illustrations.forEach { path ->
                Image(
                    painter = painterResource(path),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(140.dp).widthIn(max = 220.dp)
                )
            }
        }
        SlideText(test.instruction)
    }
}

@Composable
private fun KeyboardSlide(test: ReactionTest) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        test.responses.forEach { binding ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                KeyCap(label = binding.keyLabel)
                Text(text = "— ${binding.action.lowercase()}", style = MaterialTheme.typography.headlineSmall)
            }
        }
        SlideText(
            "Перед началом положите пальцы на " +
                (if (test.responses.size > 1) "эти клавиши" else "клавишу «${test.responses.first().keyLabel}»") +
                " и держите их так до конца теста. Мышь не используйте: во время теста она не работает. " +
                "Отвечайте нажатием клавиши; удерживать её не нужно."
        )
    }
}

@Composable
private fun PedalsSlide(test: ReactionTest) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        test.responses.forEach { binding ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                KeyCap(label = binding.pedal.toString())
                Text(text = "— ${binding.mouseTarget}", style = MaterialTheme.typography.headlineSmall)
            }
        }
        SlideText(
            "Перед началом поставьте ногу рядом с " +
                (if (test.responses.size > 1) "педалями" else test.responses.first().mouseTarget.replace("педаль", "педалью")) +
                ", как при вождении, и держите её так до конца теста. Мышь и клавиатура во время теста не работают. " +
                "Ответ засчитывается в момент нажатия педали; удерживать её не нужно."
        )
    }
}

@Composable
private fun MouseSlide(test: ReactionTest) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MouseIllustration()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                test.responses.forEach { binding ->
                    Text(
                        text = "${binding.action}: левой кнопкой по — ${binding.mouseTarget}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        if (test.responses.size > 1) {
            SlideText(
                "Курсор придётся переводить между кнопками, и время движения руки войдёт в результат. " +
                    "Для этого теста лучше выбрать клавиатуру."
            )
        } else {
            SlideText(
                "Перед началом наведите курсор на ${test.responses.first().mouseTarget} и держите его там до конца теста. " +
                    "Ответ засчитывается в момент нажатия кнопки мыши, отпускать быстро не нужно. Клавиатура во время теста не работает."
            )
        }
    }
}

@Composable
private fun PracticeSlide(device: InputDevice?) {
    SlideText(
        "Сначала будет $PRACTICE_TRIALS тренировочных попыток, чтобы привыкнуть к тесту. Они не записываются.\n\n" +
            "После тренировки появится кнопка «Начать тест» — результаты этой части будут сохранены. " +
            "Если вы уже проходили этот тест, тренировку можно пропустить — это отметится в результатах. " +
            "Перед каждой частью идёт обратный отсчёт 10 секунд: за это время " +
            when (device) {
                InputDevice.KEYBOARD -> "положите пальцы на клавиши."
                InputDevice.PEDALS -> "поставьте ногу у педали."
                else -> "наведите курсор на место ответа."
            }
    )
}

@Composable
private fun SlideText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyLarge)
}
