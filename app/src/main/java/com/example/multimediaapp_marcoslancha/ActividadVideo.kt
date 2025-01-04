package com.example.multimediaapp_marcoslancha

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class ActividadVideo : AppCompatActivity() {
    // Declaración de variables para el VideoView, ruta del archivo, y lanzador de actividad
    private lateinit var videoView: VideoView
    private lateinit var filePath: String
    private lateinit var videoCaptureLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // Vincular vistas del diseño
        videoView = findViewById(R.id.videoView)
        val btnRecord = findViewById<ImageButton>(R.id.btnRecordVideo)
        val btnPlay = findViewById<ImageButton>(R.id.btnPlayVideo)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Definir la ruta del archivo de video en el almacenamiento caché externo
        filePath = "${externalCacheDir?.absolutePath}/video.mp4"

        // Configuración del launcher para capturar video
        videoCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Video capturado correctamente
                Toast.makeText(this, "Video grabado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                // Si se cancela la grabación
                Toast.makeText(this, "Grabación de video cancelada", Toast.LENGTH_SHORT).show()
            }
        }

        // Asignar acciones a los botones
        btnRecord.setOnClickListener { captureVideo() }
        btnPlay.setOnClickListener { playVideo() }
        btnBack.setOnClickListener { finish() } // Finaliza la actividad actual
    }

    // Método para capturar video
    private fun captureVideo() {
        // Crear el archivo de video en la ruta definida
        val videoFile = File(filePath)
        val videoUri: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider", // Autoridad del FileProvider
            videoFile
        )
        // Crear un intent para capturar video
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, videoUri) // Indicar la salida del video
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) // Permitir escritura
        }
        videoCaptureLauncher.launch(intent) // Lanzar la actividad para capturar video
    }

    // Método para reproducir el video grabado
    private fun playVideo() {
        val videoFile = File(filePath)
        if (videoFile.exists()) {
            // Configurar la URI del archivo en el VideoView
            videoView.setVideoURI(Uri.fromFile(videoFile))
            videoView.start() // Iniciar la reproducción
        } else {
            // Mostrar mensaje si no se encuentra el archivo
            Toast.makeText(this, "No se encuentra el video", Toast.LENGTH_SHORT).show()
        }
    }
}
