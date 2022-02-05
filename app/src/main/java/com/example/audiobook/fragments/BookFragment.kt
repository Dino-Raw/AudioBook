package com.example.audiobook.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.audiobook.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_book.*
import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.audiobook.models.Chapter
import com.example.audiobook.viewmodels.ListBooksViewModel


class BookFragment: Fragment()  {

    private val viewModel by lazy {
        ViewModelProvider(this).get(ListBooksViewModel::class.java)
    }

    private var listChapters = arrayListOf<Chapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.fragment_book, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val bookUrl = arguments?.getString("bookUrl").toString()

        viewModel.getChapters(bookUrl).observe(viewLifecycleOwner, Observer {
            listChapters = it
        })

        viewModel.getDescription(bookUrl).observe(viewLifecycleOwner, Observer {
            setData(it)
        })


        book_player.setOnClickListener{
            val intent = Intent(this.requireActivity(), AudioFragment::class.java)

            intent.putExtra("bookTitle", arguments?.getString("bookTitle"))
            intent.putExtra("bookImgUrl", arguments?.getString("bookImgUrl"))
            intent.putExtra("listChapters", listChapters)

            startActivity(intent)
        }

        val adapterSpinner: ArrayAdapter<String> = ArrayAdapter<String>(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf("Не прослушано", "Слушаю", "Буду слушать", "Прослушано")
        )

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        book_add.adapter = adapterSpinner
        book_add.setSelection(adapterSpinner.getPosition(loadCondition()))

        book_add.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
               when(position){
                   1 -> saveCondition("Слушаю")
                   2 -> saveCondition("Буду слушать")
                   3 -> saveCondition("Прослушано")
                   else -> saveCondition("Не прослушано")
               }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

    }

    fun saveCondition(condition: String) {
        val booksList = activity?.getSharedPreferences(
            "condition_book",
            Context.MODE_PRIVATE
        )

        val booksData = activity?.getSharedPreferences(
            arguments?.getString("bookUrl").toString().replace("/", "$"),
            Context.MODE_PRIVATE
        )

        booksList?.edit()?.putString(arguments?.getString("bookUrl"), condition)?.apply()


        if(condition == "Не прослушано")
        {
            booksData?.edit()?.clear()?.apply()
            return
        }

        booksData
            ?.edit()
            ?.putString("bookTitle", arguments?.getString("bookTitle").toString())
            ?.putString("bookImgUrl", arguments?.getString("bookImgUrl").toString())
            ?.putString("bookGenre", arguments?.getString("bookGenre").toString())
            ?.putString("bookAuthor", arguments?.getString("bookAuthor").toString())
            ?.putString("bookReader", arguments?.getString("bookReader").toString())
            ?.putString("bookTime", arguments?.getString("bookTime").toString())
            ?.apply()
    }

    private fun loadCondition(): String? {

        val sharedPref = activity?.getSharedPreferences(
            "condition_book",
            Context.MODE_PRIVATE)

        return sharedPref?.getString(
            arguments?.getString("bookUrl"),
            "Не прослушано")
    }

    private fun setData(bookDescription: String){
        Picasso.get()
            .load(arguments?.getString("bookImgUrl"))
            .resize(600, 850)
            .into(book_image)

            book_title.text = arguments?.getString("bookTitle")
            book_genre.text = arguments?.getString("bookGenre")
            book_author.text = arguments?.getString("bookAuthor")
            book_reader.text = arguments?.getString("bookReader")
            book_time.text = arguments?.getString("bookTime")
            book_description.text = bookDescription
    }
}