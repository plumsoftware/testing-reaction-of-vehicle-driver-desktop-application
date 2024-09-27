package presentation.tests.traffic_light_test.store

import data.model.dto.test.TestDTO

sealed class Action {

    data class IniStartData(val testDTO: TestDTO) : Action()
    data object StartTimer : Action()
    data object GenerateRandomSignal : Action()
}