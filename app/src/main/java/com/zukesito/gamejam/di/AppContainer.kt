package com.zukesito.gamejam.di

import com.zukesito.gamejam.domain.repository.AuthRepository
import com.zukesito.gamejam.domain.repository.GameRepository

// Dependencies shared by the whole app, typed as interfaces
interface AppContainer {
    val authRepository: AuthRepository
    val gameRepository: GameRepository
}
