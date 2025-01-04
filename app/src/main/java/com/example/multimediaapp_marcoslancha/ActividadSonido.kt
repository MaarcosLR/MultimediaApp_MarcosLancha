package com.example.multimediaapp_marcoslancha

import android.Manifest
import android.annotation.SuppressLint
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

@Suppress("DEPRECATION")
class ActividadSonido : AppCompatActivity() {
    // Declaración de variables para la grabación y reproducción de audio
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

        // Solicitar permisos para grabar y almacenar audio
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1000
        )

        // Vincular vistas del diseño
        btnRecord = findViewById(R.id.btnRecord)
        btnStopRecord = findViewById(R.id.btnStopRecord)
        btnPlay = findViewById(R.id.btnPlay)
        btnStopPlay = findViewById(R.id.btnStopPlay)
        btnBack = findViewById(R.id.btnBackToMain)
        seekBar = findViewById(R.id.seekBarAudio)
        tvAudioProgress = findViewById(R.id.tvAudioProgress)

        // Crear archivo para almacenar el audio
        audioFile = File(externalCacheDir, "audio_record.3gp")

        // Configurar los botones con sus respectivas acciones
        btnRecord.setOnClickListener { startRecording() }
        btnStopRecord.setOnClickListener { stopRecording() }
        btnPlay.setOnClickListener { startPlaying() }
        btnStopPlay.setOnClickListener { stopPlaying() }
        btnBack.setOnClickListener { goBackToMainScreen() }

        // Configuración del SeekBar para reproducción de audio
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

    // Método para iniciar la grabación de audio
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

    // Método para detener la grabación
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

    // Método para iniciar la reproducción de audio
    private fun startPlaying() {
        if (!audioFile.exists()) {
            Toast.makeText(this, "No hay grabaciones disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        // Reproducir desde el inicio o continuar
        if (mediaPlayer == null || seekBar.progress == seekBar.max) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                prepare()
            }
            seekBar.progress = 0
        }

        if (wasPaused) {
            mediaPlayer?.start()
            wasPaused = false
        } else {
            mediaPlayer?.seekTo(seekBar.progress)
            mediaPlayer?.start()
        }

        Toast.makeText(this, "Reproduciendo audio...", Toast.LENGTH_SHORT).show()
        seekBar.max = mediaPlayer!!.duration
        startUpdatingSeekBar()
        btnPlay.isEnabled = false
        btnStopPlay.isEnabled = true

        mediaPlayer?.setOnCompletionListener {
            stopPlaying(resetSeekBar = true)
        }
    }

    // Método para detener la reproducción de audio
    private fun stopPlaying(resetSeekBar: Boolean = false) {
        if (mediaPlayer?.isPlaying == true) {
            // Si el audio está reproduciéndose, se pausa
            mediaPlayer?.pause()
            wasPaused = true // Indica que la reproducción fue pausada
        } else {
            // Si no está reproduciendo, liberar el MediaPlayer
            mediaPlayer?.release()
            mediaPlayer = null
            wasPaused = false // Se reinicia el estado de pausa
        }

        // Detener la actualización del SeekBar
        stopUpdatingSeekBar()

        // Si se debe resetear el SeekBar al final del archivo
        if (resetSeekBar) {
            seekBar.progress = seekBar.max // Mueve el SeekBar al final
        }

        // Habilitar y deshabilitar botones según el estado
        btnPlay.isEnabled = true
        btnStopPlay.isEnabled = false
    }

    // Método para iniciar la actualización del SeekBar
    @SuppressLint("DiscouragedApi")
    private fun startUpdatingSeekBar() {
        // Temporizador para actualizar la posición del SeekBar mientras el audio se reproduce
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    // Actualiza la posición del SeekBar en el hilo de la interfaz de usuario
                    runOnUiThread {
                        val position = player.currentPosition
                        seekBar.progress = position // Sincroniza el SeekBar con la posición actual
                        updateProgressText(position) // Actualiza el texto de progreso
                    }
                }
            }
        }, 0, 100) // Actualización cada 100 ms
    }

    // Método para detener la actualización del SeekBar
    private fun stopUpdatingSeekBar() {
        timer?.cancel() // Cancela el temporizador
        timer = null // Libera la referencia
    }

    // Método para actualizar el texto del progreso del audio
    @SuppressLint("SetTextI18n")
    private fun updateProgressText(currentPosition: Int) {
        // Calcula el tiempo actual y el tiempo total del audio
        val totalDuration = mediaPlayer?.duration ?: 0
        val currentTime = formatTime(currentPosition) // Tiempo actual formateado
        val totalTime = formatTime(totalDuration) // Tiempo total formateado

        // Actualiza el TextView con el formato "min:seg / min:seg"
        tvAudioProgress.text = "$currentTime / $totalTime"
    }

    // Método para formatear el tiempo en milisegundos a "mm:ss"
    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60 // Calcula los segundos
        val minutes = (milliseconds / 1000) / 60 // Calcula los minutos
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    // Método para volver a la pantalla principal
    private fun goBackToMainScreen() {
        val intent = Intent(this, MainActivity::class.java) // Crea un intent para MainActivity
        startActivity(intent) // Inicia la actividad principal
        finish() // Finaliza la actividad actual
    }
}
