package model.tests

import other.extension.route.DesktopRouting

data class TrafficLightDifferenceReaction(
    override val id: Int = 2,
    override val name: String = "Светофор - педаль",
    override val route: String = DesktopRouting.trafficLightDifferenceReaction,
) : ReactionTest
