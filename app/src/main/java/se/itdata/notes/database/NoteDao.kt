package se.itdata.notes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): LiveData<Note>

    @Query("UPDATE notes SET pinned = NOT pinned WHERE id = :id")
    suspend fun togglePinned(id: Int)

    @Query("UPDATE notes SET reminderTime = :time WHERE id = :id")
    suspend fun setReminder(id: Int, time: Long?)

    @Query("SELECT * FROM notes WHERE reminderTime IS NOT NULL AND reminderTime > :currentTime")
    fun getUpcomingReminders(currentTime: Long): List<Note>

}