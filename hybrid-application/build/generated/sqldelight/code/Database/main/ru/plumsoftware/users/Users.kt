package ru.plumsoftware.users

import kotlin.Long
import kotlin.String

public data class Users(
  public val user_id: Long,
  public val user_login: String,
  public val user_password: String,
  public val user_name: String,
  public val user_surname: String,
  public val user_patronymic: String?,
  public val gender: String,
)
