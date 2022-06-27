package com.example.audiobook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import com.example.audiobook.R
import com.example.audiobook.adapters.PagersAdapter
import com.example.audiobook.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val homeAdapter = PagersAdapter(childFragmentManager)
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        homeAdapter.addFragment(
            ListBooksFragment("new/?page=", "home"), " Новинки ")

        homeAdapter.addFragment(
            ListBooksFragment("popular/?w=month&page=", "home"), " Популярное ")

        homeAdapter.addFragment(
            ListBooksFragment("rating/?w=month&page=", "home"), " Рейтинг ")

        binding.homeViewPager.adapter = homeAdapter
        binding.homeTabs.setupWithViewPager(binding.homeViewPager)

        return binding.root
    }
}

