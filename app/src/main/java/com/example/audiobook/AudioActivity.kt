package com.example.audiobook

import ChapterTransfer
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.audiobook.adapters.PagersAdapter
import com.example.audiobook.fragments.ChaptersFragment
import com.example.audiobook.fragments.PlayerFragment
import com.example.audiobook.models.Chapter
import kotlinx.android.synthetic.main.activity_audio.*


class AudioActivity : FragmentActivity(), ChapterTransfer {

    private var chapterId: Int = -1
    val Broadcast_PLAY_NEW_AUDIO = "com.valdioveliu.valdio.audioplayer.PlayNewAudio"
//    private lateinit var mpService : MediaPlayerService
//    private var serviceBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        val audioAdapter = PagersAdapter(supportFragmentManager)
        val arguments = intent.extras ?: return

        val listChapters: ArrayList<Chapter> = arguments
            .getSerializable("listChapters") as ArrayList<Chapter>

        val bookUrl = arguments.getString("bookImgUrl").toString()
        val bookTitle = arguments.getString("bookTitle").toString()

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