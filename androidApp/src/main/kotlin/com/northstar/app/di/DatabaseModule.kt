package com.northstar.app.di

import android.content.Context
import androidx.room.Room
import com.northstar.app.data.local.CarNoteDao
import com.northstar.app.data.local.NorthStarDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NorthStarDatabase =
        Room.databaseBuilder(
            context,
            NorthStarDatabase::class.java,
            "app.db",
        ).build()

    @Provides
    fun provideCarNoteDao(database: NorthStarDatabase): CarNoteDao = database.carNoteDao()
}
