package se.itdata.notes

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.color.MaterialColors


class NoteEditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val surfaceColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface, Color.WHITE)
        // Setup enter transition
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = android.R.id.content
            duration = 300L
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(surfaceColor)
        }

        // Setup return transition
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = android.R.id.content
            duration = 250L
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(surfaceColor)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note_creator)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.note_creator)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
}