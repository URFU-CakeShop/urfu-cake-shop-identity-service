package ru.urfu.cake.shop.identity.service.util

import java.time.LocalDateTime
import java.time.ZoneId

object TimeUtil {
    private val ZONE = ZoneId.of("Europe/Moscow")

    fun now(): LocalDateTime = LocalDateTime.now(ZONE)
}