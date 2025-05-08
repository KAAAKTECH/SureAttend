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
    private var classesList: List<Class>
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.class_recycler_layout, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.className.text = classesList[position].className
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
