package se.itdata.notes.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import se.itdata.notes.database.NoteDao

class NoteViewModelFactory(
    private val noteDao: NoteDao,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
