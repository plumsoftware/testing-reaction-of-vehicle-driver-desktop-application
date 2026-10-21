package model.tests

import other.extension.route.DesktopRouting

/** Реакция на движущийся объект: остановка машины точно на стоп-линии. */
data class RmoStopLine(
    override val id: Int = 4,
    override val name: String = "РДО: остановка на стоп-линии",
    override val route: String = DesktopRouting.rmoStopLine,
    override val instruction: String = "Машина подъезжает к стоп-линии у красного светофора с постоянной скоростью. " +
        "Нажмите тормоз точно в тот момент, когда передний бампер машины окажется на стоп-линии — не раньше и не позже.",
    override val errorsDescription: String = "Скорость подъезда каждый раз разная. Оценивается, насколько точно вы попали " +
        "в момент пересечения линии. Нажатие, пока машина стоит, — фальстарт. Не нажали совсем — пропуск сигнала.",
    override val responses: List<ResponseBinding> = listOf(ResponseKeys.BRAKE),
    override val illustrations: List<String> = listOf(TestIllustrations.CAR, TestIllustrations.STOP_PEDAL),
) : ReactionTest

/** Простая слуховая реакция: торможение по звуковому сигналу. */
data class HornBrakeReaction(
    override val id: Int = 5,
    override val name: String = "Звуковой сигнал",
    override val route: String = DesktopRouting.hornBrakeReaction,
    override val instruction: String = "Машина едет по дороге. В случайный момент прозвучит звуковой сигнал (клаксон). " +
        "Как только услышите его, как можно быстрее нажмите тормоз. Проверьте, что звук включён и громкость достаточная.",
    override val errorsDescription: String = "Не затормозили после сигнала — пропуск сигнала. " +
        "Торможение до сигнала — фальстарт: попытка начинается заново.",
    override val responses: List<ResponseBinding> = listOf(ResponseKeys.BRAKE),
    override val illustrations: List<String> = listOf(TestIllustrations.CAR, TestIllustrations.STOP_PEDAL),
) : ReactionTest

/** PVT 10 мин (эталонная длительность) — стоп-сигналы впереди идущей машины. */
data class PvtBrakeLights10(
    override val id: Int = 6,
    override val name: String = "Бдительность (PVT), 10 мин",
    override val route: String = DesktopRouting.pvtBrakeLights10,
    override val instruction: String = "Вы едете за другой машиной. Время от времени у неё загораются стоп-сигналы. " +
        "Как только они загорятся, как можно быстрее нажмите тормоз. После нажатия на секунду покажется ваше время реакции. " +
        "Тест длится 10 минут без перерыва — старайтесь не отвлекаться до самого конца.",
    override val errorsDescription: String = "Реакция дольше 0,5 с считается провалом внимания. " +
        "Нажатие без сигнала — фальстарт.",
    override val responses: List<ResponseBinding> = listOf(ResponseKeys.BRAKE),
    override val illustrations: List<String> = listOf(TestIllustrations.CAR, TestIllustrations.STOP_PEDAL),
    override val hasFixedProtocol: Boolean = true,
) : ReactionTest

/** PVT 5 мин. Отдельный id: другая длительность — другой протокол, результаты не смешиваются. */
data class PvtBrakeLights5(
    override val id: Int = 7,
    override val name: String = "Бдительность (PVT), 5 мин",
    override val route: String = DesktopRouting.pvtBrakeLights5,
    override val instruction: String = "Вы едете за другой машиной. Время от времени у неё загораются стоп-сигналы. " +
        "Как только они загорятся, как можно быстрее нажмите тормоз. После нажатия на секунду покажется ваше время реакции. " +
        "Тест длится 5 минут без перерыва — старайтесь не отвлекаться до самого конца.",
    override val errorsDescription: String = "Реакция дольше 0,5 с считается провалом внимания. " +
        "Нажатие без сигнала — фальстарт.",
    override val responses: List<ResponseBinding> = listOf(ResponseKeys.BRAKE),
    override val illustrations: List<String> = listOf(TestIllustrations.CAR, TestIllustrations.STOP_PEDAL),
    override val hasFixedProtocol: Boolean = true,
) : ReactionTest

/** Все тесты по порядку в меню. */
val allReactionTests: List<ReactionTest>
    get() = listOf(
        TrafficLightChoiceReaction(),
        TrafficLightDifferenceReaction(),
        CarStopByRedLightSimpleReaction(),
        RmoStopLine(),
        HornBrakeReaction(),
        PvtBrakeLights10(),
        PvtBrakeLights5(),
    )

fun reactionTestName(id: Long): String = allReactionTests.find { it.id.toLong() == id }?.name ?: "№ $id"
