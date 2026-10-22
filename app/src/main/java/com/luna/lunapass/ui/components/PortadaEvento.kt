package com.luna.lunapass.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PortadaEvento(
    ruta: String,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    val imagen by produceState<Bitmap?>(
        initialValue = null,
        key1 = ruta
    ) {
        value = null

        value = withContext(Dispatchers.IO) {
            runCatching {
                val dimensiones = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

                BitmapFactory.decodeFile(ruta, dimensiones)

                var escala = 1

                while (
                    dimensiones.outWidth / escala > 1200 ||
                    dimensiones.outHeight / escala > 1200
                ) {
                    escala *= 2
                }

                val opciones = BitmapFactory.Options().apply {
                    inSampleSize = escala
                }

                BitmapFactory.decodeFile(ruta, opciones)
            }.getOrNull()
        }
    }

    val bitmap = imagen

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = descripcion,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
        )
    } else if (ruta.isNotBlank()) {
        Text("Cargando portada o imagen no disponible")
    }
}