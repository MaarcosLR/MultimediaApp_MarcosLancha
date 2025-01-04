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

        // Agregar la vista de dibujo al layout
        val dibujoView = DibujoView(this)
        val layout = findViewById<RelativeLayout>(R.id.layoutContainer) // Asegúrate de tener un contenedor como RelativeLayout
        layout.addView(dibujoView)

        // Configuración del botón
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            finish() // Cierra la actividad actual
        }
    }

    inner class DibujoView(context: Context) : View(context) {
        private val paint = Paint()
        private var desplazamiento = 0f
        private var direccion = true

        // Declarar las RectF fuera de onDraw para evitar la asignación repetida
        private val rectLogo = RectF()
        private val rectElipse = RectF()

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // Calcular las coordenadas del centro de la pantalla
            val centroX = width / 2f
            val centroY = height / 4f

            // Dibujo del logo animado (círculo azul)
            paint.color = Color.BLUE
            paint.style = Paint.Style.FILL
            canvas.drawCircle(centroX + desplazamiento, centroY, 120f, paint)

            // Rectángulo rojo alrededor del logo
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 8f
            // Actualizar la posición del rectángulo sin crear el objeto cada vez
            rectLogo.set(centroX + desplazamiento - 100f, centroY - 100f, centroX + desplazamiento + 100f, centroY + 100f)
            canvas.drawRect(rectLogo, paint)

            // Actualización de desplazamiento para la animación
            desplazamiento = if (direccion) {
                desplazamiento + 8f
            } else {
                desplazamiento - 8f
            }

            if (desplazamiento > 50 || desplazamiento < -50) {
                direccion = !direccion
            }

            // Dibujo de la elipse (centrada en la pantalla)
            paint.color = Color.GREEN // Color de la elipse
            paint.style = Paint.Style.FILL
            // Actualizar la posición de la elipse sin crear el objeto cada vez
            rectElipse.set(centroX - 200f, centroY + 200f, centroX + 200f, centroY + 350f)
            canvas.drawOval(rectElipse, paint)

            // Dibujo del texto "Marcos" en color negro, con rotación para hacerlo vertical
            val texto = "Marcos"
            paint.color = Color.BLACK
            paint.textSize = 60f
            paint.style = Paint.Style.FILL
            paint.textAlign = Paint.Align.CENTER

            // Configuración de rotación para el texto (vertical)
            canvas.save()
            canvas.rotate(-90f, centroX, centroY + 275f) // Rotar alrededor del centro de la elipse

            // Dibujo del texto "Marcos" centrado sobre la elipse
            canvas.drawText(texto, centroX, centroY + 275f, paint)

            // Restauración del canvas
            canvas.restore()

            // Redibuja la vista con animación
            postInvalidateDelayed(30)
        }
    }
}
