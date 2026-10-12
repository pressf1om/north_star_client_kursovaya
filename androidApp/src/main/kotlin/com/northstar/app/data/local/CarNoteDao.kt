package com.northstar.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CarNoteDao {
    @Query("SELECT * FROM car_notes ORDER BY id DESC")
    fun observeAll(): Flow<List<CarNoteEntity>>

    @Insert
    suspend fun insert(note: CarNoteEntity)

    @Query("DELETE FROM car_notes")
    suspend fun deleteAll()
}
