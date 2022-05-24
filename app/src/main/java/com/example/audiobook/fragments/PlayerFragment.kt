package com.example.audiobook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.audiobook.*
import com.example.audiobook.databinding.FragmentPlayerBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_player.*


class PlayerFragment() : Fragment() {
    companion object
    {
        var binding: FragmentPlayerBinding? = null

        fun setChapterData()
        {
            binding!!.remainingTimeLabel.text = AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTime
            binding!!.elapsedTimeLabel.text = AudioActivity.mediaService!!.createTimeLabel(
                AudioActivity.mediaService!!.mp!!.currentPosition.toLong())

            binding!!.positionBar.max = AudioActivity.mediaService!!.mp!!.duration
            binding!!.positionBar.progress = AudioActivity.mediaService!!.mp!!.currentPosition

            binding!!.chapterTitle.text = AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle

            if(AudioActivity.isPlaying) binding!!.playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
            else binding!!.playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val a = 0
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.visibility = View.VISIBLE
        if(AudioActivity.mediaService != null) setChapterData()
        if (AudioActivity.isPlaying)
        {
            playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp)
        } else
        {
            playBtn.setBackgroundResource(R.drawable.ic_play_black_24dp)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(!AudioActivity.serviceBound) binding!!.playBtn.isEnabled = false

        Picasso.get()
            .load(AudioActivity.bookImgUrl)
            .resize(600, 850)
            .into(binding!!.bookImg)


        binding!!.bookTitle.text = AudioActivity.bookTitle

        if(AudioActivity.mediaService != null) setChapterData()

        playBtn.setOnClickListener {
            if (!AudioActivity.isPlaying)
            {
                (activity as AudioActivity).playMedia()
            }
            else
            {
                (activity as AudioActivity).pauseMedia()
            }
        }

        nextBtn.setOnClickListener {
            if (AudioActivity.chapterIndex < AudioActivity.listChapters.size - 1) {
                (activity as AudioActivity).nextMedia()
                setChapterData()
            }
        }

        prevBtn.setOnClickListener {
            if (AudioActivity.chapterIndex > 0) {
                (activity as AudioActivity).prevMedia()
                setChapterData()
            }
        }

        binding!!.positionBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                {
                    AudioActivity.mediaService!!.mp!!.seekTo(progress)
                    AudioActivity.mediaService!!.buildNotification()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
    }
}
