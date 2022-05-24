package com.example.audiobook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import com.example.audiobook.R
import com.example.audiobook.adapters.PagersAdapter
import com.google.android.material.tabs.TabLayout


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val homeView = inflater.inflate(R.layout.fragment_home, container, false)

        val homePager: ViewPager = homeView.findViewById(R.id.home_view_pager)
        val homeTabs: TabLayout = homeView.findViewById(R.id.home_tabs)

        val homeAdapter = PagersAdapter(childFragmentManager)

        homeAdapter.addFragment(ListBooksFragment("new/", "home"), " Новинки ")
        homeAdapter.addFragment(ListBooksFragment("popular/?w=month", "home"), " Популярное ")
        homeAdapter.addFragment(ListBooksFragment("rating/?w=month", "home"), " Рейтинг ")

        homePager.adapter = homeAdapter
        homeTabs.setupWithViewPager(homePager)

        return homeView
    }
}

