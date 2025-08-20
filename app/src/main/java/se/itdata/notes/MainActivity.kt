package se.itdata.notes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import se.itdata.notes.database.AppDatabase
import se.itdata.notes.ui.adapter.NotesAdapter
import se.itdata.notes.util.NotificationHelper
import se.itdata.notes.viewmodel.NoteViewModel
import se.itdata.notes.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity(), NotesAdapter.RecyclerViewEvent {

    private lateinit var notesAdapter: NotesAdapter
    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var noteViewModel: NoteViewModel
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        checkNotificationPermission()
        NotificationHelper.createNotificationChannel(this)

        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        recyclerViewNotes.layoutManager = GridLayoutManager(this, 2)

        notesAdapter = NotesAdapter(this, emptyList(), this)
        recyclerViewNotes.adapter = notesAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val noteDao = AppDatabase.getDatabase(applicationContext).noteDao()                         // Get Dao from database instance
        val factory = NoteViewModelFactory(noteDao, applicationContext)                             // Creates ViewModel factory passing the Dao
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]         // Initializes the ViewModel using the factory

        noteViewModel.allNotes.observe(this) { notes ->
            notesAdapter.submitList(notes)
        }

        // Note creation button TODO: Expandable fab
        val fabCreateNote = findViewById<FloatingActionButton>(R.id.fabCreateNote)
        fabCreateNote.setOnClickListener {
            val intent = Intent(this, NoteEditorActivity::class.java).apply {
                intent.putExtra(NoteEditorActivity.EXTRA_MODE, "create")
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                fabCreateNote,
                ViewCompat.getTransitionName(fabCreateNote) ?: "shared_element_container"
            )

            startActivity(intent, options.toBundle())
        }
    }

    // Called when a note item is clicked, position is what note was clicked
    override fun onNoteClick(position: Int) {
        val clickedNote = notesAdapter.getNoteAt(position)
        val noteView = recyclerViewNotes.findViewHolderForAdapterPosition(position)?.itemView

        val intent = Intent(this, NoteEditorActivity::class.java).apply {
            putExtra(NoteEditorActivity.EXTRA_MODE, "edit")
            putExtra(NoteEditorActivity.EXTRA_NOTE_ID, clickedNote.id)
            putExtra("transitionName", "note_${clickedNote.id}")  // Pass the transition name for the target activity
        }

        if (noteView != null) {
            val transitionName = ViewCompat.getTransitionName(noteView) ?: "note_${clickedNote.id}"

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                noteView,
                transitionName
            )
            startActivity(intent, options.toBundle())
        } else {
            // Fallback if view is null
            startActivity(intent)
        }
    }

    override fun onTogglePinned(position: Int) {
        val clickedNote = notesAdapter.getNoteAt(position)
        noteViewModel.togglePinned(clickedNote.id)
    }

    private fun checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, safe to post notifications
                NotificationHelper.createNotificationChannel(this)
            } else {
                // Permission denied, inform the user
                Toast.makeText(
                    this,
                    "Notifications are off. Enable in Settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}