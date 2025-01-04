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
        val dibujoView = LogoDibujoView(this) // Instancia de la vista personalizada
        val layout = findViewById<RelativeLayout>(R.id.layoutContainer) // Contenedor definido en el XML
        layout.addView(dibujoView) // Agregar la vista al contenedor

        // Botón para volver a la actividad anterior
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            finish() // Finaliza la actividad actual
        }
    }

    // Clase interna personalizada para dibujar en el Canvas
    inner class LogoDibujoView(context: Context) : View(context) {
        private val paint = Paint().apply {
            isAntiAlias = true // Habilita bordes suaves para los dibujos
        }
        private var desplazamiento = 0f // Variable para animar la línea diagonal
        private var direccion = true // Dirección del desplazamiento (hacia arriba o abajo)

        // Método principal para dibujar en el Canvas
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // Dibujar el logo en la parte superior
            drawLogo(canvas)

            // Dibujar el nombre en la parte inferior, cerca del logo
            drawName(canvas)

            // Redibujar cada 30 ms para la animación
            postInvalidateDelayed(30)
        }

        // Método para dibujar el logo
        private fun drawLogo(canvas: Canvas) {
            // Coordenadas del centro de la pantalla
            val centroX = width / 2f // Ancho dividido entre 2 (centro horizontal)
            val centroY = height / 4f // Altura dividida entre 4 (posición superior)

            // **Rectángulo azul**
            paint.color = Color.BLUE // Color de relleno azul
            paint.style = Paint.Style.FILL // Estilo de relleno
            // Coordenadas del rectángulo: top-left (x1, y1) y bottom-right (x2, y2)
            canvas.drawRect(
                centroX - 150f, // x1: 150 píxeles a la izquierda del centro
                centroY - 100f, // y1: 100 píxeles arriba del centro
                centroX + 150f, // x2: 150 píxeles a la derecha del centro
                centroY + 100f, // y2: 100 píxeles abajo del centro
                paint
            )

            // **Círculo rojo**
            paint.color = Color.RED // Cambia el color a rojo
            // Dibuja un círculo centrado en el rectángulo, con radio de 80 píxeles
            canvas.drawCircle(centroX, centroY, 80f, paint)

            // **Línea diagonal animada**
            paint.color = Color.GREEN // Cambia el color a verde
            paint.strokeWidth = 8f // Ancho de la línea
            paint.style = Paint.Style.STROKE // Estilo de borde
            canvas.drawLine(
                centroX - 150f, // Punto inicial en la esquina superior izquierda del rectángulo
                centroY - 100f + desplazamiento, // Ajuste vertical según el desplazamiento
                centroX + 150f, // Punto final en la esquina inferior derecha
                centroY + 100f - desplazamiento, // Ajuste vertical inverso según el desplazamiento
                paint
            )

            // Actualización de la animación
            desplazamiento = if (direccion) desplazamiento + 4f else desplazamiento - 4f
            // Cambiar dirección si alcanza los límites
            if (desplazamiento > 50 || desplazamiento < -50) direccion = !direccion
        }

        // Método para dibujar el nombre dentro de una elipse
        private fun drawName(canvas: Canvas) {
            // Coordenadas para centrar la elipse más cerca del logo
            val centroX = width / 2f // Centro horizontal
            val centroY = height / 2.5f // Centro vertical ajustado

            // **Elipse amarilla**
            paint.color = Color.YELLOW // Cambia el color a amarillo
            paint.style = Paint.Style.FILL // Estilo de relleno
            val rectElipse = RectF( // Rectángulo delimitador de la elipse
                centroX - 200f, // x1: 200 píxeles a la izquierda del centro
                centroY - 50f, // y1: 50 píxeles arriba del centro
                centroX + 200f, // x2: 200 píxeles a la derecha del centro
                centroY + 50f  // y2: 50 píxeles abajo del centro
            )
            canvas.drawOval(rectElipse, paint) // Dibuja la elipse con las coordenadas

            // **Texto con el nombre**
            val texto = "Marcos" // Texto que se dibuja
            paint.color = Color.BLACK // Cambia el color a negro
            paint.textSize = 50f // Tamaño del texto
            paint.style = Paint.Style.FILL // Estilo de relleno
            paint.textAlign = Paint.Align.CENTER // Alineación centrada

            // Guardar el estado del canvas para rotar el texto
            canvas.save()
            canvas.rotate(-45f, centroX, centroY) // Rota el canvas 45 grados en sentido horario
            canvas.drawText(
                texto, // Dibuja el texto
                centroX, // Coordenada x (centrada horizontalmente en la elipse)
                centroY + 20f, // Coordenada y (ligeramente ajustada hacia abajo)
                paint
            )
            canvas.restore() // Restaura el estado del canvas para evitar rotaciones futuras
        }
    }
}
