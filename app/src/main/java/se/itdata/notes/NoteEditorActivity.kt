package se.itdata.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import se.itdata.notes.database.AppDatabase
import se.itdata.notes.database.Note
import se.itdata.notes.viewmodel.NoteViewModel
import se.itdata.notes.viewmodel.NoteViewModelFactory

class NoteEditorActivity : ComponentActivity() {

    lateinit var titleInput: EditText
    lateinit var contentInput: EditText

    companion object {
        const val EXTRA_MODE = "mode" // "edit" or "create"
        const val EXTRA_NOTE_ID = "note_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note_editor)

        val container = findViewById<View>(R.id.note_creator)
        ViewCompat.setTransitionName(container, intent.getStringExtra("transitionName") ?: "shared_element_container")

        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(R.id.note_creator)
            duration = 300L
            scrimColor = Color.TRANSPARENT
            interpolator = FastOutSlowInInterpolator()
        }

        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(R.id.note_creator)
            duration = 250L
            scrimColor = Color.TRANSPARENT
            interpolator = FastOutSlowInInterpolator()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.note_creator)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPadding
            )
            insets
        }

        val noteDao = AppDatabase.getDatabase(applicationContext).noteDao()                         // Get Dao from database instance
        val factory = NoteViewModelFactory(noteDao)                                                 // Creates ViewModel factory passing the Dao
        val noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]     // Initializes the ViewModel using the factory

        titleInput = findViewById(R.id.titleInput)
        contentInput = findViewById(R.id.contentInput)
        val backButton: ImageView = findViewById(R.id.arrow_back)

        val mode = intent.getStringExtra(EXTRA_MODE)
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1)

        if (mode == "edit" && noteId != -1) {
            noteViewModel.getNoteById(noteId).observe(this) {note ->
                if (note != null) {
                    titleInput.setText(note.title)
                    contentInput.setText(note.content)
                }
            }
        }
        val intentMain = Intent(this, MainActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)

        backButton.setOnClickListener {
            val title = titleInput.text.toString()
            val content = contentInput.text.toString()

            if (mode == "edit" && noteId != -1) {
                val updatedNote = Note(id = noteId, title = title, content = content)
                noteViewModel.update(updatedNote)
            } else {
                val newNote = Note(title = title, content = content)
                noteViewModel.insert(newNote)
            }
            startActivity(intentMain, options.toBundle())
        }

        val deleteButton: ImageView = findViewById(R.id.trash_bin)
        deleteButton.setOnClickListener {
            if (mode == "edit" && noteId != -1) {
                noteViewModel.getNoteById(noteId).observe(this) {note ->
                    if (note != null) {
                        noteViewModel.delete(note)
                        startActivity(intentMain, options.toBundle())
                    }
                }
            }
        }
    }
}