package com.northstar.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_notes")
data class CarNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
)
