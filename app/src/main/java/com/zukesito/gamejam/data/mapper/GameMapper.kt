package com.zukesito.gamejam.data.mapper

import com.zukesito.gamejam.data.remote.dto.GameDto
import com.zukesito.gamejam.data.remote.dto.GameRequestDto
import com.zukesito.gamejam.domain.model.Game
import java.time.LocalDate

// LocalDate uses ISO format yyyy-MM-dd, the same as the API

fun GameDto.toDomain(): Game = Game(
    id = id,
    title = title,
    platform = platform,
    hoursPlayed = hoursPlayed,
    rating = rating,
    startDate = LocalDate.parse(startDate),
    completed = completed
)

fun Game.toRequestDto(): GameRequestDto = GameRequestDto(
    title = title,
    platform = platform,
    hoursPlayed = hoursPlayed,
    rating = rating,
    startDate = startDate.toString(),
    completed = completed
)
