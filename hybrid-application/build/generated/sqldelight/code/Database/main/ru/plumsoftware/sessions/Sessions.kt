package ru.plumsoftware.sessions

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class Sessions(
  public val session_id: Long,
  public val user_id: Long,
  public val test_id: Long,
  public val test_year: Long,
  public val test_month: Long,
  public val test_day: Long,
  public val test_hour_of_day_24h: Long,
  public val test_minute_of_hour: Long,
  public val average_value: Double,
  public val standard_deviation: Double,
  public val count: Long,
  public val errors: Long,
  public val experience: Long,
  public val user_age: Long,
  public val driving_license_category: String,
  public val signal_interval: String,
)
