package com.example.audiobook.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiobook.R
import com.example.audiobook.adapters.ListBooksAdapter
import com.example.audiobook.models.Book
import kotlinx.android.synthetic.main.fragment_my_books.*
import java.io.IOException


class MyBooksFragment(val condition: String, val books: MutableList<String>): Fragment() {

    private lateinit var myBooksAdapter: ListBooksAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myBooksList = getMyBooks()

        myBooksAdapter = ListBooksAdapter("library")

        val layoutManager = LinearLayoutManager(this.context)

        list_books.layoutManager = layoutManager
        list_books.adapter = myBooksAdapter

        myBooksAdapter.add(myBooksList)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books, container, false)
    }

    fun getMyBooks(): MutableList<Book>
    {
        val listBooks = mutableListOf<Book>()

        try{
            for(bookId in 0 until books.size)
            {
                val sharedPref = activity?.getSharedPreferences(books[bookId].replace("/", "$"), Context.MODE_PRIVATE)

                val imgUrl = sharedPref?.getString("bookImgUrl","Не прослушано").toString()
                val bookTitle = sharedPref?.getString("bookTitle","Не прослушано").toString()
                val bookGenre = sharedPref?.getString("bookGenre","Не прослушано").toString()
                val bookAuthor = sharedPref?.getString("bookAuthor","Не прослушано").toString()
                val bookReader = sharedPref?.getString("bookReader","Не прослушано").toString()
                val bookTime = sharedPref?.getString("bookTime","Не прослушано").toString()

                listBooks.add(Book(imgUrl, books[bookId].replace("$", "/"), bookTitle, bookGenre, bookAuthor, bookReader, bookTime))
            }
        }
        catch(e: IOException)
        {
            e.printStackTrace()
        }
        return listBooks
    }
}