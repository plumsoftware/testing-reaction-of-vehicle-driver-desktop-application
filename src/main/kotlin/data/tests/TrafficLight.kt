package data.tests

import domain.model.ReactionTest
import presentation.other.extension.route.DesktopRouting

data class TrafficLight(
    override val name: String = "Светофор",
    override val route: String = DesktopRouting.trafficLight
) : ReactionTest