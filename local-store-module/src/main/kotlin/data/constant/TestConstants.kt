package data.constant

import data.model.regular.user.Interval

object TestConstants {
    val counts = arrayOf(1, 10, 50, 100, 150, 200, 250, 300)

    object Tests {
        val intervals: Array<Interval> = arrayOf(
            Interval(start = 1000, finish = 2000),
            Interval(start = 2000, finish = 5000),
            Interval(start = 2000, finish = 10000),
        )
    }
}