package model.tests

import other.extension.route.DesktopRouting

data class TrafficLightDifferenceReaction(
    override val id: Int = 2,
    override val name: String = "Светофор - педаль",
    override val route: String = DesktopRouting.trafficLightDifferenceReaction,
    override val instruction: String = "На экране светофор. В случайный момент загорается одна из ламп. " +
        "Если загорелся зелёный — как можно быстрее нажмите газ. На красный и жёлтый ничего не нажимайте.",
    override val errorsDescription: String = "Нажатие на красный или жёлтый — неверная реакция. " +
        "Не нажали на зелёный — пропуск сигнала. Нажатие до того, как лампа загорелась, — фальстарт.",
    override val responses: List<ResponseBinding> = listOf(ResponseKeys.GAS),
    override val illustrations: List<String> = listOf(TestIllustrations.TRAFFIC_LIGHT, TestIllustrations.GAS_PEDAL),
) : ReactionTest
