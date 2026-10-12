package com.northstar.app.di

import com.northstar.app.data.applications.ServerApplicationsRepository
import com.northstar.app.data.applications.ServerCompletedApplicationsRepository
import com.northstar.app.data.drivermessages.ServerDriverMessagesRepository
import com.northstar.app.data.local.RoomCarNotesRepository
import com.northstar.app.data.routes.ServerRoutesRepository
import com.northstar.app.data.analytics.ServerAnalyticsRepository
import com.northstar.app.data.routesettings.ServerRouteSettingsRepository
import com.northstar.app.domain.analytics.AnalyticsRepository
import com.northstar.app.data.auth.ServerUserAuthRepository
import com.northstar.app.domain.applications.ApplicationsRepository
import com.northstar.app.domain.applications.CompletedApplicationsRepository
import com.northstar.app.domain.drivermessages.DriverMessagesRepository
import com.northstar.app.domain.notes.CarNotesRepository
import com.northstar.app.domain.routes.RoutesRepository
import com.northstar.app.domain.routesettings.RouteSettingsRepository
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

    @Binds
    @Singleton
    abstract fun bindCompletedApplicationsRepository(
        impl: ServerCompletedApplicationsRepository,
    ): CompletedApplicationsRepository

    @Binds
    @Singleton
    abstract fun bindDriverMessagesRepository(impl: ServerDriverMessagesRepository): DriverMessagesRepository

    @Binds
    @Singleton
    abstract fun bindCarNotesRepository(impl: RoomCarNotesRepository): CarNotesRepository

    @Binds
    @Singleton
    abstract fun bindRoutesRepository(impl: ServerRoutesRepository): RoutesRepository

    @Binds
    @Singleton
    abstract fun bindRouteSettingsRepository(impl: ServerRouteSettingsRepository): RouteSettingsRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(impl: ServerAnalyticsRepository): AnalyticsRepository
}
