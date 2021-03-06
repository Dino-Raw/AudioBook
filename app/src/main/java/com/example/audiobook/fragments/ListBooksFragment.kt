package com.example.audiobook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiobook.R
import com.example.audiobook.adapters.ListBooksAdapter
import com.example.audiobook.viewmodels.ListBooksViewModel
import kotlinx.android.synthetic.main.fragment_list_books.*
import androidx.recyclerview.widget.RecyclerView
import java.lang.NullPointerException


class ListBooksFragment(private var url: String = "", private var type: String = "") : Fragment() {

    companion object {
        var isVisibly = false
    }

    private lateinit var adapter: ListBooksAdapter
    private val viewModel by lazy {
        ViewModelProvider(this)[ListBooksViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var pageLast = 1
        var pageCurrent = 1
        var previousTotal = 0
        val visibleThreshold = 10
        var firstVisibleItem: Int
        var visibleItemCount: Int
        var totalItemCount: Int
        var loading = false

        if (url == "" && type == "")
        {
            type = arguments?.getString("type").toString()
            url = arguments?.getString("url").toString()
        }

        if(type == "genre" || type == "search") {
            isVisibly = true
            SearchFragment.isVisibly = true
        }

        adapter = ListBooksAdapter(type)

        val layoutManager = LinearLayoutManager(this.context)

        list_books.layoutManager = layoutManager
        list_books.adapter = adapter

        viewModel.getListBooks("$url${pageCurrent}", type)
        viewModel.getLastPage(url)

        viewModel.booksLiveData.observe(viewLifecycleOwner) {
            try
            {
                if(pageCurrent <= 1) adapter.set(it)
                else adapter.add(it)
            }
            catch (e: NullPointerException)
            {
                e.printStackTrace()
            }
        }

        viewModel.lastPageLiveData.observe(viewLifecycleOwner, Observer {
            try
            {
                pageLast = it
            }
            catch (e: NullPointerException)
            {
                pageLast = 0
                e.printStackTrace()
            }
        })

        list_books.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = list_books.childCount
                totalItemCount = layoutManager.itemCount
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (loading)
                {
                    if (totalItemCount > previousTotal)
                    {
                        loading = false
                        pageCurrent++
                        previousTotal = totalItemCount
                    }
                }
                if (!loading &&
                    totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold &&
                    pageCurrent <= pageLast

                )
                {
                    viewModel.getListBooks("$url${pageCurrent}", type)
                    loading = true
                }

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_list_books, container, false)
    }
}