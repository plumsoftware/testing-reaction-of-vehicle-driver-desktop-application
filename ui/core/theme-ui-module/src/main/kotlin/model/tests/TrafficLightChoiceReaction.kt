package model.tests

import other.extension.route.DesktopRouting

data class TrafficLightChoiceReaction(
    override val id: Int = 1,
    override val name: String = "Светофор",
    override val route: String = DesktopRouting.trafficLightChoiceReaction,
    override val instruction: String = "На экране светофор. В случайный момент загорается одна из ламп — красная, жёлтая или зелёная. " +
        "Как можно быстрее ответьте, какая лампа загорелась.",
    override val errorsDescription: String = "Ответ другим цветом — неверная реакция. " +
        "Ответ до того, как лампа загорелась, — фальстарт: попытка начинается заново.",
    override val responses: List<ResponseBinding> = ResponseKeys.LAMPS,
    override val illustrations: List<String> = listOf(TestIllustrations.TRAFFIC_LIGHT),
) : ReactionTest
