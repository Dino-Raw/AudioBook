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
import java.util.ArrayList


class MyBooksFragment(): Fragment() {

    lateinit var myBooksAdapter: ListBooksAdapter
    lateinit var books : ArrayList<String>
    lateinit var condition : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        condition = arguments?.getString("condition").toString()
        books = arguments?.getStringArrayList(condition) as ArrayList<String>

        val layoutManager = LinearLayoutManager(parentFragment?.context)
        list_books.layoutManager = layoutManager

        myBooksAdapter = ListBooksAdapter("library")
        myBooksAdapter.set(getMyBooks())
        list_books.adapter = myBooksAdapter
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books, container, false)
    }

    private fun getMyBooks(): MutableList<Book>
    {
        val listBooks = mutableListOf<Book>()

        try{
            for(bookId in 0 until books.size)
            {
                val sharedPref = activity?.getSharedPreferences(books[bookId].replace("/", "$"), Context.MODE_PRIVATE)

                val imgUrl = sharedPref?.getString("bookImgUrl", "bookImgUrl").toString()
                val bookTitle = sharedPref?.getString("bookTitle","bookTitle").toString()
                val bookGenre = sharedPref?.getString("bookGenre","bookGenre").toString()
                val bookAuthor = sharedPref?.getString("bookAuthor","bookAuthor").toString()
                val bookReader = sharedPref?.getString("bookReader","bookReader").toString()
                val bookTime = sharedPref?.getString("bookTime","bookTime").toString()

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