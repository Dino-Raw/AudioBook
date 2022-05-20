package com.example.audiobook

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AudioActions : Application() {
    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_NEW_AUDIO = "ACTION_NEW_AUDIO"
        const val ACTION_SET_DATA = "ACTION_SET_DATA"
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "foreground_channel_id4"
    }
}