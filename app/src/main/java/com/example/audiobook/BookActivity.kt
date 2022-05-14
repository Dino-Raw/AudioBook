package com.example.audiobook

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_book.*
import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.audiobook.models.Chapter
import com.example.audiobook.viewmodels.ListBooksViewModel


class BookActivity: FragmentActivity()  {

    private lateinit var bookImgUrl : String

    private val viewModel by lazy {
        ViewModelProvider(this).get(ListBooksViewModel::class.java)
    }

    private var listChapters = arrayListOf<Chapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        val bookUrl = intent.extras?.getString("bookUrl").toString()

        viewModel.getChapters(bookUrl).observe(this, Observer {
            listChapters = it
        })

        viewModel.getDescription(bookUrl).observe(this, Observer {
            setData(it)
        })


        book_player.setOnClickListener{
            val intent = Intent(this, AudioActivity::class.java)

            intent.putExtra("bookTitle", book_title.text.toString())
            intent.putExtra("bookImgUrl", bookImgUrl)
            intent.putExtra("listChapters", listChapters)

            startActivity(intent)
        }

        val adapterSpinner: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf("Не прослушано", "Слушаю", "Буду слушать", "Прослушано", "Брошено")
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
                    4 -> saveCondition("Брошено")
                    else -> saveCondition("Не прослушано")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    fun saveCondition(condition: String) {
        val booksList = this.getSharedPreferences(
            "condition_book",
            Context.MODE_PRIVATE
        )

        val booksData = this.getSharedPreferences(
            intent.extras?.getString("bookUrl").toString().replace("/", "$"),
            Context.MODE_PRIVATE
        )

        booksList?.edit()?.putString(intent.extras?.getString("bookUrl"), condition)?.apply()


        if(condition == "Не прослушано")
        {
            booksData?.edit()?.clear()?.apply()
            return
        }

        booksData
            ?.edit()
            ?.putString("bookTitle", intent.extras?.getString("bookTitle").toString())
            ?.putString("bookImgUrl", intent.extras?.getString("bookImgUrl"))
            ?.putString("bookGenre", intent.extras?.getString("bookGenre").toString())
            ?.putString("bookAuthor", intent.extras?.getString("bookAuthor").toString())
            ?.putString("bookReader", intent.extras?.getString("bookReader").toString())
            ?.putString("bookTime", intent.extras?.getString("bookTime").toString())
            ?.apply()
    }

    private fun loadCondition(): String? {
        val sharedPref = this.getSharedPreferences(
            "condition_book",
            Context.MODE_PRIVATE)

        return sharedPref?.getString(
            intent.extras?.getString("bookUrl"),
            "Не прослушано")
    }

    private fun setData(bookDescription: String){
        Picasso.get()
            .load(intent.extras?.getString("bookImgUrl"))
            .resize(600, 850)
            .into(book_image)

        bookImgUrl = intent.extras?.getString("bookImgUrl").toString()
        book_title.text = intent.extras?.getString("bookTitle")
        book_genre.text = intent.extras?.getString("bookGenre")
        book_author.text = intent.extras?.getString("bookAuthor")
        book_reader.text = intent.extras?.getString("bookReader")
        book_time.text = intent.extras?.getString("bookTime")
        book_description.text = bookDescription
    }
}