package se.itdata.notes.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.itdata.notes.database.Note
import se.itdata.notes.database.NoteDao
import se.itdata.notes.util.ReminderWorker
import java.util.concurrent.TimeUnit

class NoteViewModel(private val noteDao: NoteDao, private val appContext: Context) : ViewModel() {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    fun insert(note: Note) {
        viewModelScope.launch { noteDao.insert(note) }
    }

    fun insertNoteWithReminder(note: Note, reminderTime: Long?) {
        viewModelScope.launch {
            val id = noteDao.insert(note).toInt()
            reminderTime?.let {
                noteDao.setReminder(id, it)
                scheduleWork(id, it)
            }
        }
    }

    fun update(note: Note) {
        viewModelScope.launch { noteDao.update(note) }
    }

    fun delete(note: Note) {
        viewModelScope.launch { noteDao.delete(note) }
    }

    fun togglePinned(id: Int) {
        viewModelScope.launch { noteDao.togglePinned(id) }
    }

    fun setReminder(noteId: Int, time: Long?) {
        viewModelScope.launch {
            noteDao.setReminder(noteId, time)
            time?.let { scheduleWork(noteId, it) }
        }
    }

    fun getNoteById(id: Int): LiveData<Note> = noteDao.getNoteById(id)

    suspend fun getNoteByIdSync(id: Int): Note = withContext(Dispatchers.IO) {
        noteDao.getNoteByIdSync(id)
    }

    private fun scheduleWork(noteId: Int, timeInMillis: Long) {
        val delay = (timeInMillis - System.currentTimeMillis()).coerceAtLeast(1000L)
        val data = Data.Builder().putInt("noteId", noteId).build()
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("reminder_$noteId")
            .build()

        WorkManager.getInstance(appContext)
            .enqueueUniqueWork("reminder_$noteId", ExistingWorkPolicy.REPLACE, workRequest)
    }
}
