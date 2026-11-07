package com.zukesito.gamejam.domain.repository

import com.zukesito.gamejam.domain.model.Game

// CRUD contract for games, exposes domain models only
interface GameRepository {
    suspend fun getAll(): Result<List<Game>>
    suspend fun getById(id: Int): Result<Game>
    suspend fun create(game: Game): Result<Game>
    suspend fun update(game: Game): Result<Game>
    suspend fun delete(id: Int): Result<Unit>
}
