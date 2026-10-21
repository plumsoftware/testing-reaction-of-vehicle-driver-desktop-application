package model.tests

import other.extension.route.DesktopRouting

data class CarStopByRedLightSimpleReaction(
    override val id: Int = 3,
    override val name: String = "Простая реакция",
    override val route: String = DesktopRouting.carStopByRedLightSimpleReaction,
    override val instruction: String = "Машина едет по дороге, впереди горит зелёный сигнал светофора. " +
        "Когда загорится красный, как можно быстрее нажмите тормоз.",
    override val errorsDescription: String = "Не затормозили — проезд на красный (пропуск сигнала). " +
        "Торможение на зелёный — фальстарт: попытка начинается заново.",
    override val responses: List<ResponseBinding> = listOf(ResponseKeys.BRAKE),
    override val illustrations: List<String> = listOf(TestIllustrations.CAR, TestIllustrations.TRAFFIC_LIGHT, TestIllustrations.STOP_PEDAL),
) : ReactionTest
