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
        var isVisibleSearchFragment = false
    }

    private lateinit var listBooksAdapter: ListBooksAdapter
    private val viewModel by lazy {
        ViewModelProvider(this).get(ListBooksViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var pageLast = 0
        var pageNum = 0

        var previousTotal = 0
        val visibleThreshold = 5
        var firstVisibleItem: Int
        var visibleItemCount: Int
        var totalItemCount: Int
        var loading = true

        if (url == "" && type == "")
        {
            type = arguments?.getString("type").toString()
            url = arguments?.getString("url").toString()
        }

        if(type == "search" || type == "genre") isVisibleSearchFragment = true

        listBooksAdapter = ListBooksAdapter(type)

        val layoutManager = LinearLayoutManager(this.context)

        list_books.layoutManager = layoutManager
        list_books.adapter = listBooksAdapter
        try
        {
            viewModel.getListBooks(url, type).observe(viewLifecycleOwner, Observer {

                listBooksAdapter.add(it)
            })

            viewModel.getLastPage(url).observe(viewLifecycleOwner, Observer {
                pageLast = it
            })
        }
        catch (e: NullPointerException)
        {
            e.printStackTrace()
        }
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
                        previousTotal = totalItemCount
                    }
                }
                if (!loading &&
                    totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold &&
                    pageNum <= pageLast
                )
                {
                    if(type == "search")
                    {
                        viewModel.getListBooks("$url&page=$pageNum", type)
                            .observe(viewLifecycleOwner, Observer {})
                    }
                    else if(type == "home")
                    {
                        if(url == "new/") {
                            viewModel.getListBooks("$url?page=$pageNum", type)
                                .observe(viewLifecycleOwner, Observer {})
                        }
                        else
                        {
                            viewModel.getListBooks("$url&page=$pageNum", type)
                                .observe(viewLifecycleOwner, Observer {})
                        }
                    }
                    else if(type == "genre")
                    {
                        viewModel.getListBooks(url.replace("?", "${pageNum}/?"), type)
                            .observe(viewLifecycleOwner, Observer {})
                    }
                    pageNum++
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

    override fun onDestroy() {
        super.onDestroy()
        isVisibleSearchFragment = false
    }
}