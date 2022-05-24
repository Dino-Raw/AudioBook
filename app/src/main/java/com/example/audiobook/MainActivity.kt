package com.example.audiobook

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.audiobook.databinding.ActivityMainBinding
import com.example.audiobook.models.Chapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(){
    companion object{
        var listChapters: ArrayList<Chapter> = arrayListOf()
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        nav_view.setupWithNavController(navController)
    }

    // очистка плеера
    override fun onDestroy() {
        super.onDestroy()
        if(AudioActivity.mediaService != null)
        {
            AudioActivity.mediaService!!.removeAudioFocus()
            AudioActivity.mediaService!!.removeCallStateListener()
            AudioActivity.mediaService!!.unregisterBecomingNoisyReceiver()
            AudioActivity.mediaService!!.removeNotification()
            AudioActivity.mediaService!!.stopForeground(true)
            if(AudioActivity.mediaService!!.mp!!.isPlaying)
            {
                AudioActivity.mediaService!!.mp!!.release()
            }
            AudioActivity.mediaService = null
            exitProcess(1)
        }
    }

    // выход из приложения
    override fun onBackPressed() {
        val builder = MaterialAlertDialogBuilder(this)
        builder
            .setTitle("Выйти из приложения?")
            .setPositiveButton("ДА") {_, _ -> finish() }
            .setNegativeButton("НЕТ") {dialog, _-> dialog.dismiss()}

        val customDialog = builder.create()
        customDialog.show()
    }

}