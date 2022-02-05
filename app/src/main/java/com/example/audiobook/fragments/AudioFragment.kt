package com.example.audiobook.fragments

import ChapterTransfer
import android.os.Bundle
import com.example.audiobook.R
import kotlinx.android.synthetic.main.fragment_audio.*
import androidx.fragment.app.FragmentActivity
import com.example.audiobook.adapters.PagersAdapter
import com.example.audiobook.models.Chapter


class AudioFragment : FragmentActivity(), ChapterTransfer {

    private var chapterId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_audio)

        val audioAdapter = PagersAdapter(supportFragmentManager)
        val arguments = intent.extras ?: return

        val listChapters: ArrayList<Chapter> = arguments
            .getSerializable("listChapters") as ArrayList<Chapter>
        val bookUrl = arguments.getString("bookImgUrl")
        val bookTitle = arguments.getString("bookTitle")

        //if(listChapters == null) return

        audioAdapter.addFragment(PlayerFragment(listChapters, bookUrl, bookTitle) , " Плеер ")
        audioAdapter.addFragment(ChaptersFragment(listChapters), " Главы ")

        audio_view_pager.adapter = audioAdapter
        audio_tabs.setupWithViewPager(audio_view_pager)
    }

    override fun getChapter(): Int {
        return chapterId
    }

    override fun setChapter(chapterId: Int) {
        this.chapterId = chapterId
    }


}