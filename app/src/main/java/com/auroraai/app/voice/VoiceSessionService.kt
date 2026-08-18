package com.auroraai.app.voice

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.auroraai.app.MainActivity

/**
 * Telefon kilitliyken / uygulama arka plandayken sesli sohbeti sürdüren servis.
 *
 * Bildirim panelinde 3 aksiyon sunar:
 *  - Sonlandır  -> servisi ve sohbeti tamamen kapatır
 *  - Sesi Kapat -> mikrofonu susturur (dinlemeyi durdurur)
 *  - Sesi Aç    -> mikrofonu tekrar açar
 *
 * NOT: Bu iskelet, servis yaşam döngüsünü ve bildirim kontrollerini kurar.
 * Gerçek ses akışını (STT -> AI -> gerçekçi TTS) bir sonraki aşamada
 * seçeceğiniz ses sağlayıcısının SDK'sıyla dolduracaksınız
 * (örn. ElevenLabs, Azure Speech gibi gerçekçi ses üreten servisler -
 * Android'in yerleşik TextToSpeech'i robotik kalır, "gerçek insan sesi" için
 * bu tarz bir servis şart).
 */
class VoiceSessionService : Service() {

    private var isMicMuted = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_END -> {
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_MUTE -> {
                isMicMuted = true
                updateNotification()
                return START_STICKY
            }
            ACTION_UNMUTE -> {
                isMicMuted = false
                updateNotification()
                return START_STICKY
            }
        }

        startForeground(NOTIFICATION_ID, buildNotification())
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        createChannelIfNeeded()

        val openAppIntent = Intent(this, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val endIntent = servicePendingIntent(ACTION_END, 1)
        val toggleIntent = if (isMicMuted) servicePendingIntent(ACTION_UNMUTE, 2)
                            else servicePendingIntent(ACTION_MUTE, 3)
        val toggleLabel = if (isMicMuted) "Sesi Aç" else "Sesi Kapat"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Aurora AI sesli sohbet devam ediyor")
            .setContentText(if (isMicMuted) "Mikrofon kapalı" else "Dinliyor...")
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .addAction(0, toggleLabel, toggleIntent)
            .addAction(0, "Sonlandır", endIntent)
            .build()
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun servicePendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, VoiceSessionService::class.java).apply { this.action = action }
        return PendingIntent.getService(
            this, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Sesli Sohbet", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_END = "com.auroraai.app.voice.END"
        const val ACTION_MUTE = "com.auroraai.app.voice.MUTE"
        const val ACTION_UNMUTE = "com.auroraai.app.voice.UNMUTE"
        private const val CHANNEL_ID = "voice_session"
        private const val NOTIFICATION_ID = 42
    }
}
