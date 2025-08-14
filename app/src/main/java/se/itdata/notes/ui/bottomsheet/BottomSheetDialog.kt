package se.itdata.notes.ui.bottomsheet

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import se.itdata.notes.R
import se.itdata.notes.database.AppDatabase
import se.itdata.notes.util.ReminderWorker
import se.itdata.notes.viewmodel.NoteViewModel
import se.itdata.notes.viewmodel.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class BottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

        val noteDao = AppDatabase.getDatabase(requireContext()).noteDao()
        val factory = NoteViewModelFactory(noteDao, requireContext())
        noteViewModel = ViewModelProvider(requireActivity(), factory)[NoteViewModel::class.java]

        val noteId = arguments?.getInt("noteId", -1) ?: -1
        val button1 = view.findViewById<Button>(R.id.button1)

        button1.setOnClickListener {
            val currentDateTime = Calendar.getInstance(TimeZone.getDefault())

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    TimePickerDialog(
                        requireContext(),
                        { _, hour, minute ->
                            val cal = Calendar.getInstance(TimeZone.getDefault())
                            cal.set(year, month, day, hour, minute, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            val reminderMillis = cal.timeInMillis

                            if (noteId != -1) {
                                noteViewModel.setReminder(noteId, reminderMillis)
                                scheduleWork(noteId, reminderMillis)
                            } else {
                                parentFragmentManager.setFragmentResult(
                                    "reminderRequestKey",
                                    Bundle().apply { putLong("reminderTime", reminderMillis) }
                                )
                            }

                            val simpleDateFormat = SimpleDateFormat("yyyy EEEE d MMMM, HH:mm", Locale.getDefault())
                            simpleDateFormat.timeZone = TimeZone.getDefault()
                            val reminderFormatted = simpleDateFormat.format(Date(reminderMillis))

                            Toast.makeText(context, "Reminder: $reminderFormatted", Toast.LENGTH_LONG).show()
                            dismiss()
                        },
                        currentDateTime.get(Calendar.HOUR_OF_DAY),
                        currentDateTime.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                currentDateTime.get(Calendar.YEAR),
                currentDateTime.get(Calendar.MONTH),
                currentDateTime.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        return view
    }

    private fun scheduleWork(noteId: Int, reminderMillis: Long) {
        val delay = reminderMillis - System.currentTimeMillis()
        if (delay <= 0) return

        val data = Data.Builder()
            .putInt("noteId", noteId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniqueWork(
                "reminder_$noteId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }
}
