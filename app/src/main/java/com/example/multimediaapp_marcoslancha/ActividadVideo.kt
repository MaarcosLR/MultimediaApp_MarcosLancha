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
    private lateinit var videoView: VideoView
    private lateinit var filePath: String
    private lateinit var videoCaptureLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        videoView = findViewById(R.id.videoView)
        val btnRecord = findViewById<ImageButton>(R.id.btnRecordVideo)
        val btnPlay = findViewById<ImageButton>(R.id.btnPlayVideo)
        val btnBack = findViewById<Button>(R.id.btnBack)

        filePath = "${externalCacheDir?.absolutePath}/video.mp4"

        // Configurar launcher para capturar video
        videoCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Video grabado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Grabación de video cancelada", Toast.LENGTH_SHORT).show()
            }
        }

        btnRecord.setOnClickListener { captureVideo() }
        btnPlay.setOnClickListener { playVideo() }
        btnBack.setOnClickListener { finish() }
    }

    private fun captureVideo() {
        val videoFile = File(filePath)
        val videoUri: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            videoFile
        )
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        videoCaptureLauncher.launch(intent)
    }

    private fun playVideo() {
        val videoFile = File(filePath)
        if (videoFile.exists()) {
            videoView.setVideoURI(Uri.fromFile(videoFile))
            videoView.start()
        } else {
            Toast.makeText(this, "No se encuentra el video", Toast.LENGTH_SHORT).show()
        }
    }
}
