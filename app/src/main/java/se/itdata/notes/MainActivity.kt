package se.itdata.notes

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import se.itdata.notes.database.AppDatabase
import se.itdata.notes.database.Note
import se.itdata.notes.ui.adapter.NotesAdapter
import se.itdata.notes.viewmodel.NoteViewModel
import se.itdata.notes.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewNotes)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 Columns

        notesAdapter = NotesAdapter(emptyList())
        recyclerView.adapter = notesAdapter


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Get Dao from database instance
        val noteDao = AppDatabase.getDatabase(applicationContext).noteDao()

        // Creates ViewModel factory passing the Dao
        val factory = NoteViewModelFactory(noteDao)

        // Initializes the ViewModel using the factory
        val noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        noteViewModel.allNotes.observe(this) { notes ->
            notesAdapter.submitList(notes)
        }

        val testNote = Note(
            title = "Jag gillar korv med bröd",
            content = "Fan vad gott!"
        ) // Creation of notes
        noteViewModel.insert(testNote)

        // Note creation button
        val fabCreateNote = findViewById<FloatingActionButton>(R.id.fabCreateNote)
        fabCreateNote.setOnClickListener {
            //AlertUtil.DebugAlert(this, "Title", "Message")
            val intent = Intent(this, NoteEditorActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                fabCreateNote,
                fabCreateNote.transitionName
            )
            startActivity(intent, options.toBundle())

        }

    }
}