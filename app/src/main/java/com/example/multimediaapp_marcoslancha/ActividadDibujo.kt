package com.example.multimediaapp_marcoslancha

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class ActividadDibujo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dibujo)

        // Agregar la vista personalizada al contenedor
        val dibujoView = LogoDibujoView(this)
        val layout = findViewById<RelativeLayout>(R.id.layoutContainer)
        layout.addView(dibujoView)

        // Botón para volver
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            finish()
        }
    }

    inner class LogoDibujoView(context: Context) : View(context) {
        private val paint = Paint().apply {
            isAntiAlias = true // Mejor calidad para los bordes
        }
        private var desplazamiento = 0f
        private var direccion = true

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // Dibujar el logo con formas básicas y colores
            drawLogo(canvas)

            // Dibujar el nombre en un dibujo
            drawName(canvas)

            // Redibujar para animación
            postInvalidateDelayed(30)
        }

        private fun drawLogo(canvas: Canvas) {
            // Logo en la parte superior de la pantalla
            val centroX = width / 2f
            val centroY = height / 4f

            // Rectángulo azul
            paint.color = Color.BLUE
            paint.style = Paint.Style.FILL
            canvas.drawRect(centroX - 150f, centroY - 100f, centroX + 150f, centroY + 100f, paint)

            // Círculo rojo centrado en el rectángulo
            paint.color = Color.RED
            canvas.drawCircle(centroX, centroY, 80f, paint)

            // Línea diagonal animada
            paint.color = Color.GREEN
            paint.strokeWidth = 8f
            paint.style = Paint.Style.STROKE
            canvas.drawLine(
                centroX - 150f,
                centroY - 100f + desplazamiento,
                centroX + 150f,
                centroY + 100f - desplazamiento,
                paint
            )

            // Animación de la línea
            desplazamiento = if (direccion) desplazamiento + 4f else desplazamiento - 4f
            if (desplazamiento > 50 || desplazamiento < -50) direccion = !direccion
        }

        private fun drawName(canvas: Canvas) {
            // Posición para el nombre más cerca del logo
            val centroX = width / 2f
            val centroY = height / 2.5f // Ajustar para estar más cerca del logo

            // Dibujo de una elipse amarilla
            paint.color = Color.YELLOW
            paint.style = Paint.Style.FILL
            val rectElipse = RectF(centroX - 200f, centroY - 50f, centroX + 200f, centroY + 50f)
            canvas.drawOval(rectElipse, paint)

            // Texto con el nombre "Marcos"
            val texto = "Marcos"
            paint.color = Color.BLACK
            paint.textSize = 50f
            paint.style = Paint.Style.FILL
            paint.textAlign = Paint.Align.CENTER

            // Dibujar texto inclinado
            canvas.save()
            canvas.rotate(-45f, centroX, centroY) // Rotar el texto
            canvas.drawText(texto, centroX, centroY + 20f, paint)
            canvas.restore()
        }
    }
}
