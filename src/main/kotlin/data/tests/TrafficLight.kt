package data.tests

import domain.model.ReactionTest
import presentation.extension.route.DesktopRouting

data class TrafficLight(
    override val name: String = "Светофор",
    override val route: String = DesktopRouting.trafficLight
) : ReactionTest