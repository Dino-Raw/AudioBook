package com.example.audiobook.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.example.audiobook.R
import kotlin.properties.Delegates


class SearchFragment : Fragment() {
    companion object {
        lateinit var transaction: FragmentTransaction
        lateinit var childFragment: Fragment

        var isVisibly =  false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onAttach(context: Context)
    {
        transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.child_fragment_container, childFragment).commit()

        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        isVisibly = false
    }
}