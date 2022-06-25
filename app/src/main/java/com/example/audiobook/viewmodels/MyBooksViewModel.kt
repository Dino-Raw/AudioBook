package com.example.audiobook.viewmodels

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.audiobook.objects.Book


class MyBooksViewModel() : ViewModel() {
    private val listBooks: MutableList<Book> = mutableListOf()
    private val listBooksMutableLiveData: MutableLiveData<MutableList<Book>> = MutableLiveData()
    val listBooksLiveData: LiveData<MutableList<Book>> = listBooksMutableLiveData

    fun getMyBooks(
        activity: AppCompatActivity,
        condition: String
    )// : MutableLiveData<MutableList<Book>>
    {
        val allBooks = activity.getSharedPreferences("condition_book", Context.MODE_PRIVATE)!!.all
        listBooks.clear()
        if (allBooks != null)
            for ((key, value) in allBooks)
                if(value == condition)
                {
                    val sharedPref = activity.getSharedPreferences(
                        key.replace("/", "$"),
                        Context.MODE_PRIVATE)

                    val imgUrl = sharedPref?.getString("bookImgUrl", "").toString()
                    val bookTitle = sharedPref?.getString("bookTitle", "").toString()
                    val bookGenre = sharedPref?.getString("bookGenre", "").toString()
                    val bookAuthor = sharedPref?.getString("bookAuthor", "").toString()
                    val bookReader = sharedPref?.getString("bookReader", "").toString()
                    val bookTime = sharedPref?.getString("bookTime", "").toString()
                    val bookSource = sharedPref?.getString("bookSource", "").toString()
                    val bookDescription =
                        sharedPref?.getString("bookDescription", "").toString()
                    println(key.replace("$", "/"))

                    listBooks.add(
                        Book(imgUrl,
                            key.replace("$", "/"),
                            bookTitle,
                            bookGenre,
                            bookAuthor,
                            bookReader,
                            bookTime,
                            bookDescription,
                            bookSource))
                    listBooksMutableLiveData.value = listBooks
                }

        //return listBooksMutableLiveData
    }
}