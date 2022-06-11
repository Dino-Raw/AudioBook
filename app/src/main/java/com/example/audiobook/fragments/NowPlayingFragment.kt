package com.example.audiobook.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.audiobook.AudioActivity
import com.example.audiobook.R
import com.example.audiobook.databinding.FragmentNowPlayingBinding


class NowPlayingFragment : Fragment() {
    companion object {
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE

        binding.playPauseBtnNP.setOnClickListener {
            if(AudioActivity.isPlaying) pauseMedia() else playMedia()
        }
        binding.nextBtnNP.setOnClickListener { nextMedia() }
        binding.prevBtnNP.setOnClickListener { prevMedia() }
        binding.root.setOnClickListener{ resumeMedia() }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(AudioActivity.mediaService != null)
        {
            binding.chapterTitleNp.isSelected = true
            binding.chapterTitleNp.text =
                AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle

            if(AudioActivity.isPlaying)
            {
                binding.playPauseBtnNP.setBackgroundResource(R.drawable.ic_pause_black_24dp)
            }
            else
            {
                binding.playPauseBtnNP.setBackgroundResource(R.drawable.ic_play_black_24dp)
            }
        }
    }

    private fun resumeMedia()
    {
        if(AudioActivity.mediaService != null)
        {
            val intent = Intent(requireActivity(), AudioActivity::class.java)

            intent.putExtra("class", "NowPlayingFragment")
            intent.putExtra("bookTitle", AudioActivity.bookTitle)
            intent.putExtra("bookImgUrl", AudioActivity.bookImgUrl)
            intent.putExtra("bookUrl", AudioActivity.bookUrl)

            val options = ActivityOptions.makeCustomAnimation(
                requireActivity(),
                R.anim.slide_up, R.anim.no_animation)

            startActivity(intent, options.toBundle())
        }
    }

    private fun playMedia()
    {
        AudioActivity.mediaService!!.playMedia()
        binding.playPauseBtnNP.setBackgroundResource(R.drawable.ic_pause_black_24dp)
    }

    private fun pauseMedia()
    {
        AudioActivity.mediaService!!.pauseMedia()
        binding.playPauseBtnNP.setBackgroundResource(R.drawable.ic_play_black_24dp)
    }

    private fun nextMedia()
    {
        AudioActivity.mediaService!!.nextMedia()
        binding.chapterTitleNp.text =
            AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle
    }

    private fun prevMedia()
    {
        AudioActivity.mediaService!!.prevMedia()
        binding.chapterTitleNp.text =
            AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle
    }
}