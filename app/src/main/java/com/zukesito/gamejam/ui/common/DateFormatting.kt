package com.zukesito.gamejam.ui.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DISPLAY_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-MX"))

// Example output: 15 de octubre de 2026
fun LocalDate.toDisplayText(): String = format(DISPLAY_DATE_FORMATTER)
