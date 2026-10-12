package com.northstar.app.domain.notes

import kotlinx.coroutines.flow.Flow

data class CarNote(
    val id: Long,
    val text: String,
)

interface CarNotesRepository {
    fun observeNotes(): Flow<List<CarNote>>
    suspend fun addNote(text: String)
    suspend fun clearAll()
}
