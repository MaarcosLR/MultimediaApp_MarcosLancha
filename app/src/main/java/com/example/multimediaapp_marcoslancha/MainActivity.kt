package com.example.multimediaapp_marcoslancha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnDibujo = findViewById<Button>(R.id.btnDibujo)
        val btnSonido = findViewById<Button>(R.id.btnSonido)
        val btnVideo = findViewById<Button>(R.id.btnVideo)

        btnDibujo.setOnClickListener {
            val intent = Intent(this, ActividadDibujo::class.java)
            startActivity(intent)
        }

        btnSonido.setOnClickListener {
            val intent = Intent(this, ActividadSonido::class.java)
            startActivity(intent)
        }

        btnVideo.setOnClickListener {
            val intent = Intent(this, ActividadVideo::class.java)
            startActivity(intent)
        }
    }
}
