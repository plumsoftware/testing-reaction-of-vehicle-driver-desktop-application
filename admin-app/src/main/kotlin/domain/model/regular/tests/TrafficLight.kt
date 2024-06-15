package domain.model.regular.tests

data class TrafficLight(
    override val id: Int = 1,
    override val name: String = "Светофор",
    override val route: String = "",
) : ReactionTest