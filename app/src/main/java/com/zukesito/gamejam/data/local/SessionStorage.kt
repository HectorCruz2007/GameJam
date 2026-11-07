package com.zukesito.gamejam.data.local

// Abstraction over the token storage
interface SessionStorage {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clear()
}
