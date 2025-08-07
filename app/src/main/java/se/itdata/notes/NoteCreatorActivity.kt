package se.itdata.notes

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import se.itdata.notes.database.AppDatabase
import se.itdata.notes.database.Note
import se.itdata.notes.viewmodel.NoteViewModel
import se.itdata.notes.viewmodel.NoteViewModelFactory

class NoteEditorActivity : ComponentActivity() {

    lateinit var titleInput: EditText
    lateinit var contentInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        // Get Dao from database instance
        val noteDao = AppDatabase.getDatabase(applicationContext).noteDao()

        // Creates ViewModel factory passing the Dao
        val factory = NoteViewModelFactory(noteDao)

        // Initializes the ViewModel using the factory
        val noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note_creator)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.note_creator)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        titleInput = findViewById(R.id.titleInput)
        contentInput = findViewById(R.id.contentInput)

        // Back Button
        // TODO: Create Note (store in db), switch to main activity
        val backButton: ImageView = findViewById(R.id.arrow_back)

        val intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)

        backButton.setOnClickListener {
            //Toast.makeText(this, contentInput.text, Toast.LENGTH_SHORT).show()

            val newNote = Note(
                title = titleInput.text.toString(),
                content = contentInput.text.toString()
            )
            noteViewModel.insert(newNote)
            startActivity(intent, options.toBundle())
        }

    }

}