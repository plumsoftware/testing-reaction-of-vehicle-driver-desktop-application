package data.tests

import domain.model.regular.ReactionTest
import presentation.other.extension.route.DesktopRouting

data class TrafficLight(
    override val id: Int = 1,
    override val name: String = "Светофор",
    override val route: String = DesktopRouting.trafficLight,
) : ReactionTest