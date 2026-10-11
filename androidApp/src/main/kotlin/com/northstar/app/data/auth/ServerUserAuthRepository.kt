package com.northstar.app.data.auth

import com.northstar.app.data.api.AuthApi
import com.northstar.app.data.api.LoginRequestDto
import com.northstar.app.data.api.RegisterRequestDto
import com.northstar.app.domain.auth.UserAuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerUserAuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
) : UserAuthRepository {

    override suspend fun signIn(username: String, password: String) {
        val response = authApi.login(LoginRequestDto(username = username, password = password))
        tokenStore.save(response.token)
    }

    override suspend fun signUp(username: String, email: String, password: String) {
        val response = authApi.register(
            RegisterRequestDto(username = username, email = email, password = password),
        )
        tokenStore.save(response.token)
    }

    override suspend fun signOut() {
        runCatching { authApi.logout() }
        tokenStore.clear()
    }
}
