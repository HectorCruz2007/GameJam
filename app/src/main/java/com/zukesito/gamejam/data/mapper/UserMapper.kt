package com.zukesito.gamejam.data.mapper

import com.zukesito.gamejam.data.remote.dto.UserDto
import com.zukesito.gamejam.domain.model.User

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email
)
