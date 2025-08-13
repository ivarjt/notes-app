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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import se.itdata.notes.R
import se.itdata.notes.database.AppDatabase
import java.util.Calendar
import se.itdata.notes.viewmodel.NoteViewModel
import se.itdata.notes.viewmodel.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Date

class BottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

        val noteDao = AppDatabase.getDatabase(requireContext()).noteDao()
        val factory = NoteViewModelFactory(noteDao)
        noteViewModel = ViewModelProvider(requireActivity(), factory)[NoteViewModel::class.java]

        val noteId = arguments?.getInt("noteId", -1) ?: -1
        val button1 = view.findViewById<Button>(R.id.button1)

        button1.setOnClickListener {
            val currentDateTime = Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    TimePickerDialog(
                        requireContext(),
                        { _, hour, minute ->
                            val cal = Calendar.getInstance()
                            cal.set(year, month, day, hour, minute, 0)
                            cal.isLenient = false
                            val reminderMillis = cal.timeInMillis

                            if (noteId != -1) {
                                // Edit mode → update DB immediately
                                noteViewModel.setReminder(noteId, reminderMillis)
                            } else {
                                // Create mode → pass back to Activity
                                parentFragmentManager.setFragmentResult(
                                    "reminderRequestKey",
                                    Bundle().apply {
                                        putLong("reminderTime", reminderMillis)
                                    }
                                )
                            }

                            // Swedish date format
                            val sdf = SimpleDateFormat("yyyy EEEE d MMMM, HH:mm", Locale("sv", "SE"))
                            sdf.timeZone = TimeZone.getDefault()
                            val reminderFormatted = sdf.format(Date(reminderMillis))

                            Toast.makeText(
                                context,
                                "Påminnelse: $reminderFormatted",
                                Toast.LENGTH_LONG
                            ).show()

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
}
