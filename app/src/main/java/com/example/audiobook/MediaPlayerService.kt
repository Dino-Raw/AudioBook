package com.example.audiobook

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.session.MediaSessionManager
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.audiobook.fragments.NowPlayingFragment
import com.example.audiobook.fragments.PlayerFragment
import java.io.IOException


class MediaPlayerService : Service(),
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
    AudioManager.OnAudioFocusChangeListener
{
    private var mediaSessionManager: MediaSessionManager? = null
    private lateinit var mediaSession: MediaSessionCompat
    var mp: MediaPlayer? = null
    var speed = 1F
    private var audioManager: AudioManager? = null
    //private var resumePosition = 0
    private var ongoingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null
    private val iBinder: IBinder = LocalBinder()
    private lateinit var runnable: Runnable

    override fun onBind(intent: Intent): IBinder
    {
        mediaSession = MediaSessionCompat(this, "AudioPlayer")
        mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSession.isActive = true
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        return iBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    fun setLastChapter()
    {
        getSharedPreferences("lastChapters", Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(
                AudioActivity.bookUrl,
                AudioActivity.listChapters[AudioActivity.chapterIndex].chapterUrl)
            ?.putString(
                AudioActivity.listChapters[AudioActivity.chapterIndex].chapterUrl,
                AudioActivity.chapterIndex.toString())
            ?.apply()
    }

    fun playMedia()
    {
        if (!AudioActivity.isPlaying)
        {
            AudioActivity.isPlaying = true
            NowPlayingFragment.binding.root.visibility = View.VISIBLE

            PlayerFragment
                .binding!!
                .playBtn
                .setBackgroundResource(R.drawable.ic_pause_black_24dp)

            NowPlayingFragment
                .binding
                .playPauseBtnNP
                .setBackgroundResource(R.drawable.ic_pause_black_24dp)

            buildNotification()
            //mp.seekTo(resumePosition)
            mp!!.start()
        }
    }

    private fun stopMedia()
    {
        mp!!.stop()
    }

    fun pauseMedia()
    {
        if (AudioActivity.isPlaying)
        {
            AudioActivity.isPlaying = false
            PlayerFragment
                .binding!!
                .playBtn
                .setBackgroundResource(R.drawable.ic_play_black_24dp)

            NowPlayingFragment
                .binding
                .playPauseBtnNP
                .setBackgroundResource(R.drawable.ic_play_black_24dp)

            buildNotification()
            mp!!.pause()
            //resumePosition = mp.currentPosition
        }
    }

    fun nextMedia()
    {
        if(AudioActivity.chapterIndex < AudioActivity.listChapters.size - 1)
        {
            AudioActivity.chapterIndex++
            initMediaPlayer()
            setLastChapter()
        }
    }

    fun prevMedia()
    {
        if(AudioActivity.chapterIndex > 0)
        {
            AudioActivity.chapterIndex--
            initMediaPlayer()
            setLastChapter()
        }
    }

    fun initMediaPlayer()
    {
        try
        {
            AudioActivity.isPlaying = false
            PlayerFragment.binding!!.playBtn.isEnabled = false

            if(mp == null) mp = MediaPlayer()
            mp!!.reset()
            mp!!.setOnCompletionListener(this)
            mp!!.setOnErrorListener(this)
            mp!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mp!!.setDataSource(AudioActivity.listChapters[AudioActivity.chapterIndex].chapterUrl)
            mp!!.prepareAsync()

            mp!!.setOnPreparedListener {
                setPlaybackSpeed()
                PlayerFragment.binding!!.playBtn.isEnabled = true
                initSeekBar()
                PlayerFragment.setChapterData()
                playMedia()
            }
        }
        catch (e: IOException)
        {
            e.printStackTrace()
            return
        }
    }

    fun setPlaybackSpeed()
    {
        val playbackParams: PlaybackParams = mp!!.playbackParams
        playbackParams.speed = speed
        mp!!.playbackParams = playbackParams
    }

    private fun initSeekBar(progress : Int = 0)
    {
        PlayerFragment.binding!!.elapsedTimeLabel.text =
            createTimeLabel(mp!!.currentPosition.toLong())

        PlayerFragment.binding!!.remainingTimeLabel.text =
            createTimeLabel(mp!!.duration.toLong())

        PlayerFragment.binding!!.positionBar.max = mp!!.duration
        PlayerFragment.binding!!.positionBar.progress = progress
    }

    fun setupSeekBar()
    {
        runnable = Runnable {
            PlayerFragment.binding!!.elapsedTimeLabel.text =
                createTimeLabel(mp!!.currentPosition.toLong())

            PlayerFragment.binding!!.positionBar.progress = mp!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    fun createTimeLabel(time: Long): String {
        var timeLabel = ""
        val hour = time / 1000 / 60 / 60
        val min = time / 1000 / 60 % 60
        val sec = time / 1000 % 60

        if (hour != 0L)
        {
            timeLabel = "$hour:"
            if(min < 10) timeLabel += "0"
        }

        timeLabel += "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    // когда завершается проигрывание медиа
    override fun onCompletion(mp: MediaPlayer?) {
        if (mp != null &&
            mp.duration.toLong() > 100L &&
            mp.duration.toLong() - mp.currentPosition.toLong() <= 100L)
                nextMedia()
    }

    // обработка ошибок асинхронных операций
    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what)
        {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ->
                Log.d("MediaPlayer Error",
                "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra")
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d("MediaPlayer Error",
                "MEDIA ERROR SERVER DIED $extra")
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d("MediaPlayer Error",
                "MEDIA ERROR UNKNOWN $extra")
        }
        return false
    }

    inner class LocalBinder : Binder() {
        val service: MediaPlayerService
            get() = this@MediaPlayerService
    }

    fun removeAudioFocus() { audioManager!!.abandonAudioFocus(this) }

    // метод фокусировки звука
    override fun onAudioFocusChange(focusChange: Int) {
        //когда обновляется аудиофокус системы
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN ->
            {
                playMedia()
                mp!!.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS ->
            {
                stopMedia()
                mp!!.release()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                pauseMedia()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                if (mp!!.isPlaying) mp!!.setVolume(0.1f, 0.1f)
        }
    }

    // пауза при снятии наушников
    private val becomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            pauseMedia()
        }
    }

    // регистрация события снятия наушников
    fun registerBecomingNoisyReceiver()
    {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    fun unregisterBecomingNoisyReceiver() { unregisterReceiver(becomingNoisyReceiver) }

    fun initAudioManager()
    {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager!!.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN)
    }

    // реализация PhoneStateListener
    // прослушивает TelephonyManager
    // действия во время звонков
    fun initCallStateListener() {
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String)
            {
                if (mp != null)
                    when (state) {
                        TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING ->
                        {
                            pauseMedia()
                            ongoingCall = true
                        }

                        TelephonyManager.CALL_STATE_IDLE ->
                        {
                            if (ongoingCall)
                            {
                                ongoingCall = false
                                playMedia()
                            }
                        }
                    }
            }
        }
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    fun removeCallStateListener()
    {
        if (phoneStateListener != null)
            telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    // создание уведомления
    fun buildNotification() {
        val playPause = if(AudioActivity.isPlaying) 1 else 0

        val playPauseIcon = if(AudioActivity.isPlaying)
            R.drawable.ic_pause_black_24dp else R.drawable.ic_play_black_24dp

        val playPauseTitle = if(AudioActivity.isPlaying)
            AudioActions.ACTION_PAUSE else AudioActions.ACTION_PLAY

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val notificationChannel = NotificationChannel(AudioActions.CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_DEFAULT)

            notificationChannel.vibrationPattern = longArrayOf(0)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            // seekbar в уведомлении
            val playbackSpeed = if(AudioActivity.isPlaying) speed else 0F

            mediaSession
                .setMetadata(MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mp!!.duration.toLong())
                .build())

            val playBackState = PlaybackStateCompat
                .Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING,
                    mp!!.currentPosition.toLong(),
                    playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()

            mediaSession.setPlaybackState(playBackState)

            mediaSession.setCallback(object: MediaSessionCompat.Callback(){
                override fun onSeekTo(pos: Long)
                {
                    super.onSeekTo(pos)
                    mp!!.seekTo(pos.toInt())

                    val playBackStateNew = PlaybackStateCompat
                        .Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING,
                            mp!!.currentPosition.toLong(),
                            playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()

                    mediaSession.setPlaybackState(playBackStateNew)
                }
            })
        }

        val notification = NotificationCompat.Builder(baseContext, AudioActions.CHANNEL_ID)

        notification
            .setContentIntent(playbackAction(5))
            .setStyle(androidx
                .media
                .app
                .NotificationCompat
                .MediaStyle()
                .setMediaSession(mediaSession.sessionToken))
            .setSmallIcon(R.drawable.ic_library_black_24dp)
            .setContentTitle(AudioActivity.bookTitle)
            .setContentText(AudioActivity.listChapters[AudioActivity.chapterIndex].chapterTitle)
            .setVibrate(null)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_skip_previous_black_24dp,
                AudioActions.ACTION_PREVIOUS,
                playbackAction(3))
            .addAction(playPauseIcon,
                playPauseTitle,
                playbackAction(playPause))
            .addAction(R.drawable.ic_skip_next_black_24dp,
                AudioActions.ACTION_NEXT,
                playbackAction(2))
            .addAction(R.drawable.ic_baseline_close_24,
                AudioActions.ACTION_STOP,
                playbackAction(4))

        startForeground(AudioActions.NOTIFICATION_ID, notification.build())
    }

    // удаление уведомления
    fun removeNotification()
    {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(AudioActions.NOTIFICATION_ID)
    }

    // генерация действий для уведомления
    private fun playbackAction(actionNumber: Int): PendingIntent?
    {
        var playbackAction = Intent(baseContext, NotificationReceiver::class.java)
        when (actionNumber) {
            0 -> {
                playbackAction.action = AudioActions.ACTION_PLAY
                return PendingIntent.getBroadcast(
                    baseContext,
                    actionNumber,
                    playbackAction,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }
            1 -> {
                playbackAction.action = AudioActions.ACTION_PAUSE
                return PendingIntent.getBroadcast(
                    baseContext,
                    actionNumber,
                    playbackAction,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }
            2 -> {
                playbackAction.action = AudioActions.ACTION_NEXT
                return PendingIntent.getBroadcast(
                    baseContext,
                    actionNumber,
                    playbackAction,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }
            3 -> {
                playbackAction.action = AudioActions.ACTION_PREVIOUS
                return PendingIntent.getBroadcast(
                    baseContext,
                    actionNumber,
                    playbackAction,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }
            4 -> {
                playbackAction.action = AudioActions.ACTION_STOP
                return PendingIntent.getBroadcast(
                    baseContext,
                    actionNumber,
                    playbackAction,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }
            5 -> {
                playbackAction = Intent(baseContext, AudioActivity::class.java)
                playbackAction.putExtra("class", "NowPlayingFragment")
                playbackAction.putExtra("bookTitle", AudioActivity.bookTitle)
                playbackAction.putExtra("bookImgUrl", AudioActivity.bookImgUrl)
                playbackAction.putExtra("bookUrl", AudioActivity.bookUrl)

                return PendingIntent.getActivity(
                    this,
                    actionNumber,
                    playbackAction,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }
            else -> return null
        }
    }
}