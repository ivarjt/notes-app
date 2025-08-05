package se.itdata.notes.util

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

object AlertUtil {

    // Creates a simple alert dialog, mostly used for debugging
    fun DebugAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(context,
                android.R.string.yes, Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(context,
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

}