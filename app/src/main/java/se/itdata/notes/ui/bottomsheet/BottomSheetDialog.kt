package se.itdata.notes.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import se.itdata.notes.R

class BottomSheetDialog : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(
            R.layout.bottom_sheet_layout,
            container, false
        )

        val button1 = view.findViewById<Button>(R.id.button1)
        //val button2 = view.findViewById<Button>(R.id.button2)

        button1.setOnClickListener { // Choose date and time
            Toast.makeText(activity, "First Button Clicked", Toast.LENGTH_SHORT).show()
            dismiss()
        }
/*
        button2.setOnClickListener { // TODO: REMOVE, Test Button
            Toast.makeText(activity, "Second Button Clicked", Toast.LENGTH_SHORT).show()
            dismiss()
        }*/
        return view
    }
}