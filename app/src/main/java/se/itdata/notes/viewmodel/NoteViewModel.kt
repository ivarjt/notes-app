package se.itdata.notes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import se.itdata.notes.database.Note
import se.itdata.notes.database.NoteDao

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    fun insert(note: Note) {
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }

    fun update(note: Note) {
        viewModelScope.launch {
            noteDao.update(note)
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }

    fun getNoteById(id: Int): LiveData<Note> {
        return noteDao.getNoteById(id)
    }

    fun togglePinned(id: Int) {
        viewModelScope.launch {
            noteDao.togglePinned(id)
        }
    }

}