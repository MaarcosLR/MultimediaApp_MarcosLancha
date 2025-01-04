package com.example.multimediaapp_marcoslancha

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vincular botones del diseño
        val btnDibujo = findViewById<Button>(R.id.btnDibujo)
        val btnSonido = findViewById<Button>(R.id.btnSonido)
        val btnVideo = findViewById<Button>(R.id.btnVideo)

        // Navegar a la actividad de dibujo
        btnDibujo.setOnClickListener {
            val intent = Intent(this, ActividadDibujo::class.java)
            startActivity(intent)
        }

        // Navegar a la actividad de sonido
        btnSonido.setOnClickListener {
            val intent = Intent(this, ActividadSonido::class.java)
            startActivity(intent)
        }

        // Navegar a la actividad de video
        btnVideo.setOnClickListener {
            val intent = Intent(this, ActividadVideo::class.java)
            startActivity(intent)
        }
    }
}
