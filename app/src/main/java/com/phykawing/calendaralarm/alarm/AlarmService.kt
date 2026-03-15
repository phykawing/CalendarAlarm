package com.phykawing.calendaralarm.alarm

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.phykawing.calendaralarm.R
import com.phykawing.calendaralarm.domain.model.AlarmType
import com.phykawing.calendaralarm.ui.alarm.AlarmActivity
import com.phykawing.calendaralarm.util.Constants

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var audioFader: AudioFader? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val eventId = intent?.getLongExtra(Constants.EXTRA_EVENT_ID, -1) ?: -1
        val eventTitle = intent?.getStringExtra(Constants.EXTRA_EVENT_TITLE) ?: "Calendar Event"
        val alarmTypeName = intent?.getStringExtra(Constants.EXTRA_ALARM_TYPE) ?: AlarmType.SOUND.name
        val soundUriString = intent?.getStringExtra(Constants.EXTRA_SOUND_URI)

        if (eventId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(Constants.ALARM_NOTIFICATION_ID, buildNotification(eventId, eventTitle))

        val alarmType = try {
            AlarmType.valueOf(alarmTypeName)
        } catch (_: Exception) {
            AlarmType.SOUND
        }

        when (alarmType) {
            AlarmType.SOUND -> startSoundAlarm(soundUriString)
            AlarmType.VIBRATION -> startVibrationAlarm()
            AlarmType.SOUND_VIBRATION -> {
                startSoundAlarm(soundUriString)
                startVibrationAlarm()
            }
        }

        return START_STICKY
    }

    private fun startSoundAlarm(soundUriString: String?) {
        val uri = if (soundUriString != null) {
            Uri.parse(soundUriString)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(applicationContext, uri)
            isLooping = true
            prepare()
            start()
        }

        audioFader = AudioFader(mediaPlayer!!).also { it.startFadeIn() }
    }

    private fun startVibrationAlarm() {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator = vibratorManager.defaultVibrator

        val pattern = longArrayOf(0, 500, 200, 500, 200, 500, 1000)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    private fun buildNotification(eventId: Long, title: String): Notification {
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            putExtra(Constants.EXTRA_EVENT_ID, eventId)
            putExtra(Constants.EXTRA_EVENT_TITLE, title)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, eventId.toInt(), fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Calendar Alarm")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        audioFader?.stop()
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        vibrator?.cancel()
        super.onDestroy()
    }
}
