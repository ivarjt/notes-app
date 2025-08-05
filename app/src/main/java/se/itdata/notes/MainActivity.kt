package se.itdata.notes

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import se.itdata.notes.database.AppDatabase
import se.itdata.notes.database.Note
import se.itdata.notes.viewmodel.NoteViewModel
import se.itdata.notes.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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


        //val testNote = Note(title="Testing Title", content = "This is a test noteer KEKEKEK") // Creation of notes
        //noteViewModel.insert(testNote)

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