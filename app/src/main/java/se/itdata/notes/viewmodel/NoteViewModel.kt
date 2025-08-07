package se.itdata.notes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import se.itdata.notes.database.Note
import se.itdata.notes.database.NoteDao

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

}