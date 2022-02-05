package com.example.audiobook.viewholders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.audiobook.R
import com.example.audiobook.models.Book
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.book.view.*


class ListBooksViewHolder(itemView: View, type: String) : RecyclerView.ViewHolder(itemView){

    private val imgUrl: ImageView = itemView.row_img
    private var bookImgUrl: String = ""
    private var bookUrl= ""
    private val bookTitle: TextView = itemView.row_title
    private val bookGenre: TextView = itemView.row_genre
    private val bookAuthor: TextView = itemView.row_author
    private val bookReader: TextView = itemView.row_reader
    private val bookTime: TextView = itemView.row_time

    init{
        itemView.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("bookUrl", bookUrl)
            bundle.putString("bookTitle", bookTitle.text.toString())
            bundle.putString("bookGenre", bookGenre.text.toString())
            bundle.putString("bookAuthor", bookAuthor.text.toString())
            bundle.putString("bookReader", bookReader.text.toString())
            bundle.putString("bookTime", bookTime.text.toString())
            bundle.putString("bookImgUrl", bookImgUrl)

            when(type){
                "search" -> itemView
                    .findNavController()
                    .navigate(R.id.action_listBooksFragment_to_navigation_book, bundle)

                "genre" -> itemView
                    .findNavController()
                    .navigate(R.id.action_listBooksFragment_to_navigation_book, bundle)

                "home" -> itemView
                    .findNavController()
                    .navigate(R.id.action_navigation_home_to_navigation_book, bundle)

                "library" -> itemView
                    .findNavController()
                    .navigate(R.id.action_navigation_library_to_navigation_book, bundle)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(books: Book){
        bookTitle.text = books.bookTitle
        bookGenre.text = books.bookGenre
        bookAuthor.text = books.bookAuthor
        bookReader.text = books.bookReader
        bookTime.text = books.bookTime
        bookUrl = books.bookUrl
        bookImgUrl = books.imgUrl

        Picasso.get()
            .load(books.imgUrl)
            .tag(itemView.context)
            .into(imgUrl)

    }
}