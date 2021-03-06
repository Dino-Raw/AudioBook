package com.example.audiobook.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import com.example.audiobook.AudioActions
import com.example.audiobook.activities.AudioActivity
import com.example.audiobook.R
import com.example.audiobook.fragments.NowPlayingFragment
import com.example.audiobook.fragments.PlayerFragment

// реализация действий уведомления
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action)
        {
            AudioActions.ACTION_PLAY -> playMedia()
            AudioActions.ACTION_PAUSE -> pauseMedia()
            AudioActions.ACTION_NEXT -> nextMedia()
            AudioActions.ACTION_PREVIOUS -> prevMedia()
            AudioActions.ACTION_STOP -> stopMedia()
        }
    }

    private fun playMedia()
    {
        AudioActivity.mediaService!!.playMedia()

        PlayerFragment
            .binding!!
            .playBtn
            .setBackgroundResource(R.drawable.ic_pause_black_24dp)

        NowPlayingFragment
            .binding
            .playPauseBtnNP
            .setBackgroundResource(R.drawable.ic_pause_black_24dp)
    }

    private fun pauseMedia()
    {
        AudioActivity.mediaService!!.pauseMedia()
        PlayerFragment
            .binding!!
            .playBtn
            .setBackgroundResource(R.drawable.ic_play_black_24dp)

        NowPlayingFragment
            .binding
            .playPauseBtnNP
            .setBackgroundResource(R.drawable.ic_play_black_24dp)
    }

    private fun nextMedia()
    {

        if (AudioActivity.chapterIndex < AudioActivity.listChapters.size - 1)
        {
            AudioActivity.chapterIndex++

            NowPlayingFragment.binding.chapterTitleNp.text =
                AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle

            AudioActivity.mediaService!!.initMediaPlayer()
        }
    }

    private fun prevMedia()
    {
        if (AudioActivity.chapterIndex > 0)
        {
            AudioActivity.chapterIndex--

            NowPlayingFragment.binding.chapterTitleNp.text =
                AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle

            AudioActivity.mediaService!!.initMediaPlayer()
        }
    }

    private fun stopMedia()
    {
        AudioActivity.mediaService!!.pauseMedia()
        AudioActivity.mediaService!!.stopForeground(true)
        AudioActivity.isPlaying = false
        PlayerFragment.binding!!.playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
        NowPlayingFragment.binding.root.visibility = View.INVISIBLE
    }
}