package se.itdata.notes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.itdata.notes.R
import se.itdata.notes.database.Note

class NotesAdapter(private var dataSet: List<Note>) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = itemView.findViewById(R.id.textViewTitle)
        val contentView: TextView = itemView.findViewById(R.id.textViewContent)
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = dataSet[position]
        holder.titleView.text = note.title
        holder.contentView.text = note.content
    }

    override fun getItemCount(): Int = dataSet.size

    fun submitList(newData: List<Note>) {
        dataSet = newData
        notifyDataSetChanged()
    }

}