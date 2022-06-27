package com.example.audiobook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.audiobook.R
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.audiobook.activities.AudioActivity


class ChaptersFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val chapter = mutableListOf<String>()
        for(i in 0 until AudioActivity.listChapters.size)
        {
            chapter.add("Название: ${ AudioActivity.listChapters[i].chapterTitle}\n" +
                    "Длительность: ${ AudioActivity.listChapters[i].chapterTime}")
        }

        val chapterView = inflater.inflate(
            R.layout.fragment_chapters,
            container,
            false
        )

        val listChapters: ListView = chapterView.findViewById(R.id.list_chapters)

        val adapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_list_item_1, chapter
        )

        listChapters.adapter = adapter
        adapter.notifyDataSetChanged()

        listChapters.setOnItemClickListener {
                parent, view, position, id ->
            AudioActivity.chapterIndex = position
            AudioActivity.mediaService!!.initMediaPlayer()
            AudioActivity.mediaService!!.setLastChapter()
        }

        return chapterView
    }
}