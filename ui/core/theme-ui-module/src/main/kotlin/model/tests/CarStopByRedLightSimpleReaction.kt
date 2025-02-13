package model.tests

import other.extension.route.DesktopRouting

data class CarStopByRedLightSimpleReaction(
    override val id: Int = 3,
    override val name: String = "Простая реакция",
    override val route: String = DesktopRouting.carStopByRedLightSimpleReaction,
) : ReactionTest
