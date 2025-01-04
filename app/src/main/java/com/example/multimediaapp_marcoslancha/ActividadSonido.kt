package com.example.multimediaapp_marcoslancha

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

class ActividadSonido : AppCompatActivity() {
    private lateinit var btnRecord: ImageButton
    private lateinit var btnStopRecord: ImageButton
    private lateinit var btnPlay: ImageButton
    private lateinit var btnStopPlay: ImageButton
    private lateinit var btnBack: Button
    private lateinit var seekBar: SeekBar
    private lateinit var tvAudioProgress: TextView

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioFile: File
    private var timer: Timer? = null
    private var wasPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sonido)

        // Solicitar permisos
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1000
        )

        // Inicializar vistas
        btnRecord = findViewById(R.id.btnRecord)
        btnStopRecord = findViewById(R.id.btnStopRecord)
        btnPlay = findViewById(R.id.btnPlay)
        btnStopPlay = findViewById(R.id.btnStopPlay)
        btnBack = findViewById(R.id.btnBackToMain)
        seekBar = findViewById(R.id.seekBarAudio)
        tvAudioProgress = findViewById(R.id.tvAudioProgress)

        // Archivo de audio
        audioFile = File(externalCacheDir, "audio_record.3gp")

        btnRecord.setOnClickListener { startRecording() }
        btnStopRecord.setOnClickListener { stopRecording() }
        btnPlay.setOnClickListener { startPlaying() }
        btnStopPlay.setOnClickListener { stopPlaying() }
        btnBack.setOnClickListener { goBackToMainScreen() }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer?.seekTo(progress) // Cambiar posición en reproducción
                    updateProgressText(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile.absolutePath)
            prepare()
            start()
        }
        Toast.makeText(this, "Grabando audio...", Toast.LENGTH_SHORT).show()
        btnRecord.isEnabled = false
        btnStopRecord.isEnabled = true
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        Toast.makeText(this, "Grabación finalizada", Toast.LENGTH_SHORT).show()
        btnRecord.isEnabled = true
        btnStopRecord.isEnabled = false
    }

    private fun startPlaying() {
        if (!audioFile.exists()) {
            Toast.makeText(this, "No hay grabaciones disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        // Inicializar el MediaPlayer si es nulo o si el SeekBar está al final
        if (mediaPlayer == null || seekBar.progress == seekBar.max) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                prepare()
            }
            seekBar.progress = 0 // Reiniciar al principio si el SeekBar está al final
        }

        // Si se pausó, continuar desde la posición actual
        if (wasPaused) {
            mediaPlayer?.start()
            wasPaused = false
        } else {
            // Reproducir desde la posición actual del SeekBar
            mediaPlayer?.seekTo(seekBar.progress)
            mediaPlayer?.start()
        }

        Toast.makeText(this, "Reproduciendo audio...", Toast.LENGTH_SHORT).show()

        // Configurar el SeekBar y temporizador
        seekBar.max = mediaPlayer!!.duration
        startUpdatingSeekBar()

        btnPlay.isEnabled = false
        btnStopPlay.isEnabled = true

        mediaPlayer?.setOnCompletionListener {
            stopPlaying(resetSeekBar = true)
        }
    }

    private fun stopPlaying(resetSeekBar: Boolean = false) {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            wasPaused = true
        } else {
            mediaPlayer?.release()
            mediaPlayer = null
            wasPaused = false
        }
        stopUpdatingSeekBar()

        if (resetSeekBar) {
            seekBar.progress = seekBar.max // Forzar el SeekBar al máximo
        }

        btnPlay.isEnabled = true
        btnStopPlay.isEnabled = false
    }

    private fun startUpdatingSeekBar() {
        // Reducir el intervalo de actualización a 100ms para mayor precisión
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    runOnUiThread {
                        val position = player.currentPosition
                        seekBar.progress = position
                        updateProgressText(position)
                    }
                }
            }
        }, 0, 100) // Actualización más frecuente (cada 100 ms)
    }

    private fun stopUpdatingSeekBar() {
        timer?.cancel()
        timer = null
    }

    private fun updateProgressText(currentPosition: Int) {
        val totalDuration = mediaPlayer?.duration ?: 0
        val currentTime = formatTime(currentPosition)
        val totalTime = formatTime(totalDuration)
        tvAudioProgress.text = "$currentTime / $totalTime"
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / 1000) / 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun goBackToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
