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

        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onResume() {
        super.onResume()
        val libraryAdapter = PagersAdapter(childFragmentManager)

        libraryAdapter.addFragment(newInstance("Слушаю"), "Слушаю")
        libraryAdapter.addFragment(newInstance("Буду слушать"), "Буду слушать")
        libraryAdapter.addFragment(newInstance("Прослушано"), "Прослушано")
        libraryAdapter.addFragment(newInstance("Брошено"), "Брошено")

        library_view_pager.adapter = libraryAdapter
        library_tabs.setupWithViewPager(library_view_pager)
    }

    private fun newInstance(condition: String): MyBooksFragment
    {
        val arguments = Bundle()

        arguments.putString("condition", condition)

        val fragment = MyBooksFragment()
        fragment.arguments = arguments

        return fragment
    }
}