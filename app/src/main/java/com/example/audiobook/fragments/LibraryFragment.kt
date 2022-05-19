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


class LibraryFragment : Fragment() {
    private var update = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        println("!!!!!!!!!!!!!CREATE VIEW!!!!!!!!!!!!!!!!")
        println(update)
        update = false

        val libraryView = inflater.inflate(R.layout.fragment_library, container, false)
        val libraryPager: ViewPager = libraryView.findViewById(R.id.library_view_pager)
        val libraryTabs: TabLayout = libraryView.findViewById(R.id.library_tabs)
        val libraryAdapter = PagersAdapter(childFragmentManager)

        libraryAdapter.addFragment(MyBooksFragment("Слушаю", getBooks("Слушаю")) , " Слушаю ")
        libraryAdapter.addFragment(MyBooksFragment("Буду слушать", getBooks("Буду слушать")) , " Буду слушать ")
        libraryAdapter.addFragment(MyBooksFragment("Прослушано", getBooks("Прослушано")) , " Прослушано ")
        libraryAdapter.addFragment(MyBooksFragment("Брошено", getBooks("Брошено")) , " Брошено ")

        libraryPager.adapter = libraryAdapter
        libraryTabs.setupWithViewPager(libraryPager)

        println("!!!!!!!!!!!!!CREATED VIEW!!!!!!!!!!!!!!!!")
        println(update)

        return libraryView
    }

    override fun onStop() {
        println("!!!!!!!!!!!!!STOP!!!!!!!!!!!!!!!!")
        println(update)

        super.onStop()

        update = true

        println("!!!!!!!!!!!!!STOPPED!!!!!!!!!!!!!!!!")
        println(update)
    }

    override fun onResume() {
        super.onResume()
        if(update)
        {
            println("@@@@@@@@@@@@@@@@@")
            fragmentManager?.beginTransaction()?.detach(this)?.commit()
            fragmentManager?.beginTransaction()?.attach(this)?.commit()
            println("#################")
            update = false
            library_view_pager.adapter?.notifyDataSetChanged()
        }
    }

//    override fun onStart() {
//        println("!!!!!!!!!!!!!START!!!!!!!!!!!!!!!!")
//        println(update)
//
//        super.onStart()
//
//        if(update)
//        {
//            println("@@@@@@@@@@@@@@@@@")
//            //getFragmentManager()?.beginTransaction()?.detach(this)?.commit()
//            //getFragmentManager()?.beginTransaction()?.attach(this)?.commit()
//
//            val a = getFragmentManager()?.beginTransaction()
//            a?.detach(this)?.commit()
//            a?.attach(this)?.commit()
//            a?.commit()
//
//            println("#################")
//            update = false
//        }
//
//        println("!!!!!!!!!!!!!STARTED!!!!!!!!!!!!!!!!")
//        println(update)
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        println("!!!!!!!!!!!!!CREATED ACTIVITY!!!!!!!!!!!!!!!!")
        println(update)
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (userVisibleHint) {
//            getFragmentManager()?.beginTransaction()?.detach(this)?.commit()
//            getFragmentManager()?.beginTransaction()?.attach(this)?.commit()
//        }
//    }

    private fun getBooks(type: String): MutableList<String>
    {
        val books = mutableListOf<String>()
        val sharedPref = activity?.getSharedPreferences("condition_book", Context.MODE_PRIVATE)
        val allBooks = sharedPref?.all

        if (allBooks != null) {
            for ((key, value) in allBooks)
            {
                if(value == type) {
                    books.add(key)
                }
            }
        }

        return books
    }

}