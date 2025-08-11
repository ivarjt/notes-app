package se.itdata.notes.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import se.itdata.notes.R
import se.itdata.notes.database.Note

class NotesAdapter(private val context: Context,
    private var dataSet: List<Note>,
    private val listener: RecyclerViewEvent) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val titleView: TextView = itemView.findViewById(R.id.textViewTitle)
        val contentView: TextView = itemView.findViewById(R.id.textViewContent)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onNoteClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = dataSet[position]
        holder.titleView.text = note.title
        holder.contentView.text = note.content

        ViewCompat.setTransitionName(holder.itemView, "note_${note.id}")
    }

    override fun getItemCount(): Int = dataSet.size

    fun getNoteAt(position: Int): Note {
        return dataSet[position]
    }


    fun submitList(newData: List<Note>) {
        dataSet = newData
        notifyDataSetChanged()
    }

    interface RecyclerViewEvent {
        fun onNoteClick(position: Int)
    }

}