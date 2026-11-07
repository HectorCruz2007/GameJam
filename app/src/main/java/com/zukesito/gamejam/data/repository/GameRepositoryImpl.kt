package com.zukesito.gamejam.data.repository

import com.zukesito.gamejam.data.local.SessionStorage
import com.zukesito.gamejam.data.mapper.toDomain
import com.zukesito.gamejam.data.mapper.toRequestDto
import com.zukesito.gamejam.data.remote.api.GameApiService
import com.zukesito.gamejam.data.remote.safeApiCall
import com.zukesito.gamejam.domain.model.Game
import com.zukesito.gamejam.domain.repository.GameRepository

class GameRepositoryImpl(
    private val gameApiService: GameApiService,
    private val sessionStorage: SessionStorage
) : GameRepository {

    override suspend fun getAll(): Result<List<Game>> = call {
        gameApiService.getGames().map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Game> = call {
        gameApiService.getGame(id).toDomain()
    }

    override suspend fun create(game: Game): Result<Game> = call {
        gameApiService.createGame(game.toRequestDto()).toDomain()
    }

    override suspend fun update(game: Game): Result<Game> = call {
        gameApiService.updateGame(game.id, game.toRequestDto()).toDomain()
    }

    override suspend fun delete(id: Int): Result<Unit> = call {
        gameApiService.deleteGame(id)
    }

    // An expired token clears the session so the UI can go back to login
    private suspend fun <T> call(block: suspend () -> T): Result<T> =
        safeApiCall(onUnauthorized = sessionStorage::clear, block = block)
}
