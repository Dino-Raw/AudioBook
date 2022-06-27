package com.example.audiobook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiobook.R
import com.example.audiobook.adapters.ListBooksAdapter
import com.example.audiobook.viewmodels.MyBooksViewModel
import kotlinx.android.synthetic.main.fragment_my_books.*


class MyBooksFragment(): Fragment() {

    private lateinit var myBooksAdapter: ListBooksAdapter
    lateinit var condition : String

    private val viewModel by lazy {
        ViewModelProvider(this)[MyBooksViewModel()::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        condition = arguments?.getString("condition").toString()

        val layoutManager = LinearLayoutManager(parentFragment?.context)
        list_books.layoutManager = layoutManager

        myBooksAdapter = ListBooksAdapter("library")

        viewModel.getMyBooks(activity as AppCompatActivity, condition)
        viewModel.listBooksLiveData.observe(viewLifecycleOwner) {
            myBooksAdapter.set(it)
        }

        list_books.adapter = myBooksAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books, container, false)
    }


}