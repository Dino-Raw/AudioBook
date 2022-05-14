package com.example.audiobook.viewholders

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.audiobook.BookActivity
import com.example.audiobook.models.Book
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.book.view.*


class ListBooksViewHolder(itemView: View, type: String) : RecyclerView.ViewHolder(itemView){

    private var imgUrl: ImageView = itemView.row_img
    private var bookImgUrl: String = ""
    private var bookUrl = ""
    private var bookTitle: TextView = itemView.row_title
    private var bookGenre: TextView = itemView.row_genre
    private var bookAuthor: TextView = itemView.row_author
    private var bookReader: TextView = itemView.row_reader
    private var bookTime: TextView = itemView.row_time

    init{
        itemView.setOnClickListener{
            val intent = Intent(itemView.context, BookActivity::class.java)

            intent.putExtra("bookUrl", bookUrl)
            intent.putExtra("bookTitle", bookTitle.text.toString())
            intent.putExtra("bookGenre", bookGenre.text.toString())
            intent.putExtra("bookAuthor", bookAuthor.text.toString())
            intent.putExtra("bookReader", bookReader.text.toString())
            intent.putExtra("bookTime", bookTime.text.toString())
            intent.putExtra("bookImgUrl", bookImgUrl)

            itemView.context.startActivity(intent)
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