package com.northstar.app.data.local

import com.northstar.app.domain.notes.CarNote
import com.northstar.app.domain.notes.CarNotesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomCarNotesRepository @Inject constructor(
    private val carNoteDao: CarNoteDao,
) : CarNotesRepository {

    override fun observeNotes(): Flow<List<CarNote>> =
        carNoteDao.observeAll().map { list ->
            list.map { CarNote(id = it.id, text = it.text) }
        }

    override suspend fun addNote(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        carNoteDao.insert(CarNoteEntity(text = trimmed))
    }

    override suspend fun clearAll() {
        carNoteDao.deleteAll()
    }
}
