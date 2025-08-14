package se.itdata.notes.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import se.itdata.notes.database.AppDatabase

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val noteId = inputData.getInt("noteId", -1)
        if (noteId == -1) return Result.failure()

        val noteDao = AppDatabase.getDatabase(applicationContext).noteDao()
        val note = noteDao.getNoteByIdSync(noteId) ?: return Result.failure()

        showNotification(note.id, note.title, note.content)

        return Result.success()
    }

    private fun showNotification(noteId: Int, title: String, content: String) {
        val context = applicationContext
        val channelId = "note_reminder_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Note Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(noteId, notification)
    }
}
