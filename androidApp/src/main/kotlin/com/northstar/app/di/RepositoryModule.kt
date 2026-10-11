package com.northstar.app.di

import com.northstar.app.data.auth.ServerUserAuthRepository
import com.northstar.app.domain.auth.UserAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserAuthRepository(impl: ServerUserAuthRepository): UserAuthRepository
}
