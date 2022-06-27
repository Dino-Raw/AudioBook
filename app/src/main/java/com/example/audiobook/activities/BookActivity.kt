package com.example.audiobook.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.audiobook.R
import com.example.audiobook.models.Chapter
import com.example.audiobook.viewmodels.BookViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.activity_book.*


class BookActivity: AppCompatActivity()  {

    private lateinit var bookImgUrl : String
    private lateinit var bookDescription : String
    private lateinit var bookSource : String
    private lateinit var bookImg : RequestCreator

    private val viewModel by lazy {
        ViewModelProvider(this)[BookViewModel::class.java]
    }

    private var listChapters = arrayListOf<Chapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)


        val bookUrl = intent.extras?.getString("bookUrl").toString()

        viewModel.getChapters(bookUrl)
        viewModel.getSource(bookUrl)
        viewModel.getDescription(bookUrl)

        viewModel.chaptersLiveData.observe(this, Observer { listChapters = it })

        viewModel.descriptionLiveData.observe(this, Observer {
            bookDescription = it
            if(intent.extras?.getString("bookDescription") == "")
                book_description.text = bookDescription
        })

        viewModel.sourceLiveData.observe(this, Observer {
            bookSource = it
            if(intent.extras?.getString("bookSource") == "")
                book_source.text = bookSource
        })

        setData()

        book_player.setOnClickListener{
            val intent = Intent(this, AudioActivity::class.java)
            MainActivity.listChapters = listChapters

            if(AudioActivity.bookUrl != null &&
                AudioActivity.bookUrl == bookUrl &&
                AudioActivity.mediaService != null)
            {
                intent.putExtra("class", "Now")
                intent.putExtra("bookTitle", AudioActivity.bookTitle)
                intent.putExtra("bookImgUrl", AudioActivity.bookImgUrl)
                intent.putExtra("bookUrl", AudioActivity.bookUrl)
            }

            else
            {
                intent.putExtra("class", "First")
                intent.putExtra("bookTitle", book_title.text.toString())
                intent.putExtra("bookImgUrl", bookImgUrl)
                intent.putExtra("bookUrl", bookUrl)
            }

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

            override fun onNothingSelected(parent: AdapterView<*>) {}
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

        if(booksData.all.isEmpty())
            booksData
                ?.edit()
                ?.putString("bookTitle", intent.extras?.getString("bookTitle").toString())
                ?.putString("bookImgUrl", intent.extras?.getString("bookImgUrl"))
                ?.putString("bookGenre", intent.extras?.getString("bookGenre").toString())
                ?.putString("bookAuthor", intent.extras?.getString("bookAuthor").toString())
                ?.putString("bookReader", intent.extras?.getString("bookReader").toString())
                ?.putString("bookTime", intent.extras?.getString("bookTime").toString())
                ?.putString("bookSource", bookSource)
                ?.putString("bookDescription", bookDescription)
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

    private fun setData(){

        bookImg = Picasso.get()
            .load(intent.extras?.getString("bookImgUrl"))
            .resize(600, 850)
        bookImg.into(book_image)

        bookImgUrl = intent.extras?.getString("bookImgUrl").toString()
        book_title.text = intent.extras?.getString("bookTitle")
        book_genre.text = intent.extras?.getString("bookGenre")
        book_author.text = intent.extras?.getString("bookAuthor")
        book_reader.text = intent.extras?.getString("bookReader")
        book_time.text = intent.extras?.getString("bookTime")
        book_source.text = intent.extras?.getString("bookSource")
        book_description.text = intent.extras?.getString("bookDescription")
    }
}