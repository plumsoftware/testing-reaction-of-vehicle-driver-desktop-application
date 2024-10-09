package model.tests

import other.extension.route.DesktopRouting

data class TrafficLightChoiceReaction(
    override val id: Int = 1,
    override val name: String = "Светофор",
    override val route: String = DesktopRouting.trafficLightChoiceReaction,
) : ReactionTest