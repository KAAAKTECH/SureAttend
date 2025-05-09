package com.kaak.sureattend.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kaak.sureattend.R
import com.kaak.sureattend.dataclass.Class

class ClassAdapter(
    private var classesList: List<Class>,
    private val onItemLongClick: (Class, Int) -> Unit,
    private val onItemClick: (Class, Int) -> Unit,
    private val isItemSelected: (Class) -> Boolean
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.class_recycler_layout, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentClass = classesList[position]
        holder.className.text = currentClass.className

        // Visual selection indicator (you can customize this)
        holder.itemView.isSelected = isItemSelected(currentClass)
        holder.itemView.setBackgroundResource(
            if (isItemSelected(currentClass)) R.drawable.bg_recycler_item_selected
            else R.drawable.bg_recycler_item
        )

        holder.itemView.setOnClickListener {
            onItemClick(currentClass, position)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(currentClass, position)
            true
        }
    }

    override fun getItemCount(): Int = classesList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Class>) {
        classesList = newList
        notifyDataSetChanged()
    }

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val className: TextView = itemView.findViewById(R.id.className)
    }
}
