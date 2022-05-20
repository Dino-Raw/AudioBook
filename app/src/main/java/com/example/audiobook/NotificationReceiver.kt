package com.example.audiobook

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import com.example.audiobook.fragments.PlayerFragment
import kotlinx.android.synthetic.main.fragment_player.*
import java.io.IOException

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
        AudioActivity.isPlayed = true
        AudioActivity.mediaService!!.playMedia()
        AudioActivity.mediaService!!.buildNotification()
        PlayerFragment.binding.playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
    }

    private fun pauseMedia()
    {
        AudioActivity.isPlayed = false
        AudioActivity.mediaService!!.pauseMedia()
        AudioActivity.mediaService!!.buildNotification()
        PlayerFragment.binding.playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
    }

    private fun nextMedia()
    {
        if (AudioActivity.chapterIndex < AudioActivity.listChapters.size - 1) {
            AudioActivity.isPlayed = true
            AudioActivity.chapterIndex++

            PlayerFragment.binding.chapterTitle.text = AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle
            PlayerFragment.binding.remainingTimeLabel.text = AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTime

            AudioActivity.mediaService!!.stopMedia()

            AudioActivity.mediaService!!.initMediaPlayer()

            AudioActivity.mediaService!!.mp.setOnPreparedListener {
                playMedia()
                AudioActivity.mediaService!!.initSeekBar()
            }
        }
    }

    private fun prevMedia()
    {
        if (AudioActivity.chapterIndex > 0) {
            AudioActivity.isPlayed = true
            AudioActivity.chapterIndex--

            PlayerFragment.binding.chapterTitle.text = AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle
            PlayerFragment.binding.remainingTimeLabel.text = AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTime

            AudioActivity.mediaService!!.stopMedia()

            AudioActivity.mediaService!!.initMediaPlayer()

            AudioActivity.mediaService!!.mp.setOnPreparedListener {
                playMedia()
                AudioActivity.mediaService!!.initSeekBar()
            }
        }
    }

    private fun stopMedia()
    {
        AudioActivity.mediaService!!.stopMedia()
        AudioActivity.mediaService!!.stopForeground(true)
        AudioActivity.mediaService!!.removeNotification()
        AudioActivity.mediaService = null
        AudioActivity.serviceBound = false
        AudioActivity.isPlayed = false
        PlayerFragment.binding.playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
    }
}