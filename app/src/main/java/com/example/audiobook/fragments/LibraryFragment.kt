package com.example.audiobook.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.example.audiobook.R
import com.example.audiobook.adapters.PagersAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_library.*
import java.util.ArrayList


class LibraryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        println("-----------CREATE_VIEW_LIBRARY--------------------")

        val libraryView = inflater.inflate(R.layout.fragment_library, container, false)

        val libraryPager: ViewPager = libraryView.findViewById(R.id.library_view_pager)

        val libraryTabs: TabLayout = libraryView.findViewById(R.id.library_tabs)
        val libraryAdapter = PagersAdapter(childFragmentManager)

        libraryAdapter.addFragment(newInstance("Слушаю"), "Слушаю")
        libraryAdapter.addFragment(newInstance("Буду слушать"), "Буду слушать")
        libraryAdapter.addFragment(newInstance("Прослушано"), "Прослушано")
        libraryAdapter.addFragment(newInstance("Брошено"), "Брошено")

        libraryPager.adapter = libraryAdapter
        libraryTabs.setupWithViewPager(libraryPager)

        return libraryView
    }

    override fun onResume() {
        super.onResume()
        println("-----------RESUME_LIBRARY--------------------")
    }

    private fun newInstance(condition: String): MyBooksFragment
    {
        val arguments = Bundle()
        val fragment = MyBooksFragment()

        arguments.putString("condition", condition)
        arguments.putStringArrayList(condition, getBooks(condition))

        fragment.arguments = arguments

        return fragment
    }

    private fun getBooks(type: String): ArrayList<String>
    {
        val books = arrayListOf<String>()
        val sharedPref = activity?.getSharedPreferences("condition_book", Context.MODE_PRIVATE)
        val allBooks = sharedPref!!.all

        if (allBooks != null)
            for ((key, value) in allBooks)
                if(value == type)
                    books.add(key)

        return books
    }

}