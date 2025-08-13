package se.itdata.notes.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val pinned: Boolean = false, // true if pinned, false if not pinned
    val reminderTime: Long? = null // Date+time in millis
)
