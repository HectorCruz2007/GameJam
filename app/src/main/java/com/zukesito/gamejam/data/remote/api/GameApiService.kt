package com.zukesito.gamejam.data.remote.api

import com.zukesito.gamejam.data.remote.dto.GameDto
import com.zukesito.gamejam.data.remote.dto.GameRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// The token is added by AuthInterceptor, not here
interface GameApiService {

    @GET("games")
    suspend fun getGames(): List<GameDto>

    @GET("games/{id}")
    suspend fun getGame(@Path("id") id: Int): GameDto

    @POST("games")
    suspend fun createGame(@Body body: GameRequestDto): GameDto

    @PUT("games/{id}")
    suspend fun updateGame(@Path("id") id: Int, @Body body: GameRequestDto): GameDto

    @DELETE("games/{id}")
    suspend fun deleteGame(@Path("id") id: Int)
}
