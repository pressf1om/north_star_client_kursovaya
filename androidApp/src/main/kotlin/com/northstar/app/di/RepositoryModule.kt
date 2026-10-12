package com.northstar.app.di

import com.northstar.app.data.applications.ServerApplicationsRepository
import com.northstar.app.data.auth.ServerUserAuthRepository
import com.northstar.app.domain.applications.ApplicationsRepository
import com.northstar.app.data.cars.ServerCarsRepository
import com.northstar.app.data.users.ServerUsersRepository
import com.northstar.app.domain.cars.CarsRepository
import com.northstar.app.domain.auth.UserAuthRepository
import com.northstar.app.domain.users.UsersRepository
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

    @Binds
    @Singleton
    abstract fun bindUsersRepository(impl: ServerUsersRepository): UsersRepository

    @Binds
    @Singleton
    abstract fun bindCarsRepository(impl: ServerCarsRepository): CarsRepository

    @Binds
    @Singleton
    abstract fun bindApplicationsRepository(impl: ServerApplicationsRepository): ApplicationsRepository
}
