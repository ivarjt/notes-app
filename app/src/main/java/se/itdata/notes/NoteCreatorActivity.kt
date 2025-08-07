package se.itdata.notes

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NoteEditorActivity : ComponentActivity() {

    lateinit var titleInput: EditText
    lateinit var contentInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note_creator)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.note_creator)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back Button
        val backButton: ImageView = findViewById(R.id.arrow_back)
        backButton.setOnClickListener {
            Toast.makeText(this, contentInput.text, Toast.LENGTH_SHORT).show()

        }

        titleInput = findViewById(R.id.titleInput)
        contentInput = findViewById(R.id.contentInput)

    }

}