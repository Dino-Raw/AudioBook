package com.example.audiobook.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.audiobook.R
import com.example.audiobook.adapters.PagersAdapter
import com.google.android.material.tabs.TabLayout


class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val libraryView = inflater.inflate(R.layout.fragment_library, container, false)

        val libraryPager: ViewPager = libraryView.findViewById(R.id.library_view_pager)
        val libraryTabs: TabLayout = libraryView.findViewById(R.id.library_tabs)

        val libraryAdapter = PagersAdapter(childFragmentManager)

        libraryAdapter.addFragment(MyBooksFragment("Слушаю", getBooks("Слушаю")) , " Слушаю ")
        libraryAdapter.addFragment(MyBooksFragment("Буду слушать", getBooks("Буду слушать")) , " Буду слушать ")
        libraryAdapter.addFragment(MyBooksFragment("Прослушано", getBooks("Прослушано")) , " Прослушано ")

        libraryPager.adapter = libraryAdapter
        libraryTabs.setupWithViewPager(libraryPager)

        return libraryView
    }

    private fun getBooks(type: String): MutableList<String> {
        val books = mutableListOf<String>()
        val sharedPref = activity?.getSharedPreferences("condition_book", Context.MODE_PRIVATE)
        val allBooks = sharedPref?.all

        if (allBooks != null) {
            for ((key, value) in allBooks) {
                if(value == type)
                    books.add(key)
            }
        }
        return books
    }

}