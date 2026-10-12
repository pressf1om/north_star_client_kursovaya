package com.northstar.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CarNoteEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class NorthStarDatabase : RoomDatabase() {
    abstract fun carNoteDao(): CarNoteDao
}
