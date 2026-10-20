package model.tests

import other.extension.route.DesktopRouting

/** Реакция на движущийся объект: остановка машины точно на стоп-линии. */
data class RmoStopLine(
    override val id: Int = 4,
    override val name: String = "РДО: остановка на стоп-линии",
    override val route: String = DesktopRouting.rmoStopLine,
) : ReactionTest

/** Простая слуховая реакция: торможение по звуковому сигналу. */
data class HornBrakeReaction(
    override val id: Int = 5,
    override val name: String = "Звуковой сигнал",
    override val route: String = DesktopRouting.hornBrakeReaction,
) : ReactionTest

/** PVT 10 мин (эталонная длительность) — стоп-сигналы впереди идущей машины. */
data class PvtBrakeLights10(
    override val id: Int = 6,
    override val name: String = "Бдительность (PVT), 10 мин",
    override val route: String = DesktopRouting.pvtBrakeLights10,
) : ReactionTest

/** PVT 5 мин. Отдельный id: другая длительность — другой протокол, результаты не смешиваются. */
data class PvtBrakeLights5(
    override val id: Int = 7,
    override val name: String = "Бдительность (PVT), 5 мин",
    override val route: String = DesktopRouting.pvtBrakeLights5,
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
