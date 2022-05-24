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
    lateinit var condition : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        condition = arguments?.getString("condition").toString()

        val layoutManager = LinearLayoutManager(parentFragment?.context)
        list_books.layoutManager = layoutManager

        myBooksAdapter = ListBooksAdapter("library")
        myBooksAdapter.set(getMyBooks())
        list_books.adapter = myBooksAdapter
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
        val allBooks = activity?.getSharedPreferences("condition_book", Context.MODE_PRIVATE)!!.all
        val listBooks = mutableListOf<Book>()

        if (allBooks != null)
            for ((key, value) in allBooks)
                if(value == condition)
                {
                    val sharedPref = activity?.getSharedPreferences(key.replace("/", "$"), Context.MODE_PRIVATE)
                    val imgUrl = sharedPref?.getString("bookImgUrl", "bookImgUrl").toString()
                    val bookTitle = sharedPref?.getString("bookTitle","bookTitle").toString()
                    val bookGenre = sharedPref?.getString("bookGenre","bookGenre").toString()
                    val bookAuthor = sharedPref?.getString("bookAuthor","bookAuthor").toString()
                    val bookReader = sharedPref?.getString("bookReader","bookReader").toString()
                    val bookTime = sharedPref?.getString("bookTime","bookTime").toString()

                    listBooks.add(Book(imgUrl, key.replace("$", "/"), bookTitle, bookGenre, bookAuthor, bookReader, bookTime))

                }
        return listBooks
    }
}