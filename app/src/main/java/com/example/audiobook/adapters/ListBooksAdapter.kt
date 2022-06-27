package com.example.audiobook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.audiobook.R
import com.example.audiobook.models.Book
import com.example.audiobook.viewholders.ListBooksViewHolder

class ListBooksAdapter(private val type: String) : RecyclerView.Adapter<ListBooksViewHolder>(){

    private val listBooks = mutableListOf<Book>()

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ListBooksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.book, parent, false)
        return ListBooksViewHolder(view, type)
    }

    override fun getItemCount(): Int = listBooks.size

    override fun onBindViewHolder(holder: ListBooksViewHolder, position: Int) {
        holder.bind(listBooks[position])
    }

    fun set(list: MutableList<Book>)
    {
        this.listBooks.clear()
        this.listBooks.addAll(list)
        notifyDataSetChanged()
    }

    fun add(list: MutableList<Book>)
    {
        this.listBooks.addAll(list)
        notifyDataSetChanged()
    }
}