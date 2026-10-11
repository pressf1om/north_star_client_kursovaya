package com.northstar.app.domain.auth

interface UserAuthRepository {
    suspend fun signIn(username: String, password: String)
    suspend fun signUp(username: String, email: String, password: String)
    suspend fun signOut()
}
