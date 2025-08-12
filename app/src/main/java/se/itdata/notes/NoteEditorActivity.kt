package se.itdata.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
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

    private lateinit var noteViewModel: NoteViewModel
    private var mode: String? = null
    private var noteId: Int = -1

    private lateinit var pinToggleButton: ImageView
    private var currentNote: Note? = null
    private var isPinnedLocal = false

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

        val noteDao = AppDatabase.getDatabase(applicationContext).noteDao()
        val factory = NoteViewModelFactory(noteDao)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        titleInput = findViewById(R.id.titleInput)
        contentInput = findViewById(R.id.contentInput)
        val backButton: ImageView = findViewById(R.id.arrow_back)
        val deleteButton: ImageView = findViewById(R.id.trash_bin)
        pinToggleButton = findViewById(R.id.buttonPinToggle)

        mode = intent.getStringExtra(EXTRA_MODE)
        noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1)

        if (mode == "edit" && noteId != -1) {
            noteViewModel.getNoteById(noteId).observe(this) { note ->
                if (note != null) {
                    currentNote = note
                    titleInput.setText(note.title)
                    contentInput.setText(note.content)
                    updatePinIcon(note.pinned)
                }
            }
        }

        val intentMain = Intent(this, MainActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)

        backButton.setOnClickListener {
            saveNote()
            startActivity(intentMain, options.toBundle())
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveNote()
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        })

        deleteButton.setOnClickListener {
            deleteNote()
            startActivity(intentMain, options.toBundle())
        }

        pinToggleButton.setOnClickListener {
            if (mode == "edit" && currentNote != null) {
                // Editing existing note → update DB immediately
                noteViewModel.togglePinned(currentNote!!.id)
            } else {
                // Creating new note → just toggle local variable
                isPinnedLocal = !isPinnedLocal
                updatePinIcon(isPinnedLocal)
            }
        }

    }

    private fun updatePinIcon(isPinned: Boolean) {
        if (isPinned) {
            pinToggleButton.setImageResource(R.drawable.keep_filled)
        } else {
            pinToggleButton.setImageResource(R.drawable.keep_outline)
        }
    }

    private fun saveNote() {
        val currentTitle = titleInput.text.toString()
        val currentContent = contentInput.text.toString()

        if (currentTitle.isBlank() && currentContent.isBlank()) {
            return
        } else {
            if (mode == "edit" && noteId != -1) {
                val updatedNote = currentNote?.copy(
                    title = currentTitle,
                    content = currentContent
                ) ?: Note(id = noteId, title = currentTitle, content = currentContent)
                noteViewModel.update(updatedNote)
            } else {
                val newNote = Note(
                    title = currentTitle,
                    content = currentContent,
                    pinned = isPinnedLocal // store pinned state
                )
                noteViewModel.insert(newNote)
            }
        }
    }


    private fun deleteNote() {
        if (mode == "edit" && noteId != -1) {
            noteViewModel.getNoteById(noteId).observe(this) { note ->
                if (note != null) {
                    noteViewModel.delete(note)
                }
            }
        }
    }

}
