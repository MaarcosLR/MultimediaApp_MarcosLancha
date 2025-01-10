package com.example.multimediaapp_marcoslancha

import android.content.Intent
import android.content.pm.PackageManager
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

    // Variables para el manejo del VideoView y archivo de video
    private lateinit var videoView: VideoView
    private lateinit var filePath: String
    private lateinit var videoCaptureLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    // Código de solicitud para los permisos
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // Enlace de los elementos del diseño
        videoView = findViewById(R.id.videoView)
        val btnRecord = findViewById<ImageButton>(R.id.btnRecordVideo)
        val btnPlay = findViewById<ImageButton>(R.id.btnPlayVideo)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Definir la ruta para almacenar el video grabado
        filePath = "${externalCacheDir?.absolutePath}/video.mp4"

        // Configurar el lanzador para manejar la captura de video
        videoCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Mensaje al usuario si la grabación fue exitosa
                Toast.makeText(this, "Video grabado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                // Mensaje al usuario si se canceló la grabación
                Toast.makeText(this, "Grabación cancelada", Toast.LENGTH_SHORT).show()
            }
        }

        // Asignar acciones a los botones
        btnRecord.setOnClickListener { checkAndRequestPermissions() } // Grabar video con permisos
        btnPlay.setOnClickListener { playVideo() } // Reproducir video grabado
        btnBack.setOnClickListener { finish() } // Regresar a la pantalla anterior
    }

    /**
     * Verifica y solicita los permisos necesarios para usar la cámara.
     */
    private fun checkAndRequestPermissions() {
        val cameraPermission = android.Manifest.permission.CAMERA

        // Si el permiso no está otorgado, se solicita al usuario
        if (checkSelfPermission(cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(cameraPermission), PERMISSION_REQUEST_CODE)
        } else {
            // Si el permiso ya está otorgado, inicia la captura de video
            captureVideo()
        }
    }

    /**
     * Maneja el resultado de la solicitud de permisos.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado
                captureVideo()
            } else {
                // Permiso denegado, muestra un mensaje al usuario
                Toast.makeText(this, "Permiso de cámara requerido para grabar video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Captura un video utilizando la cámara del dispositivo.
     */
    private fun captureVideo() {
        val videoFile = File(filePath)
        val videoUri: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            videoFile
        )

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, videoUri) // Indica la salida del video
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) // Permitir escritura
        }
        videoCaptureLauncher.launch(intent)
    }

    /**
     * Reproduce el video grabado en el `VideoView`.
     */
    private fun playVideo() {
        val videoFile = File(filePath)
        if (videoFile.exists()) {
            // Configura la URI del video en el VideoView y comienza la reproducción
            videoView.setVideoURI(Uri.fromFile(videoFile))
            videoView.start()
        } else {
            // Muestra un mensaje si no se encuentra el archivo de video
            Toast.makeText(this, "No se encontró el video", Toast.LENGTH_SHORT).show()
        }
    }
}
