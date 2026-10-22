package com.luna.lunapass.ui.components

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.luna.lunapass.model.Asistente
import java.io.File

@Composable
fun EntradaQrDialog(
    asistente: Asistente,
    nombreEvento: String,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(asistente.id) {
        generarQr("LUNAPASS:${asistente.id}")
    }

    AlertDialog(
        onDismissRequest = onCerrar,

        title = {
            Text("Entrada LunaPass")
        },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment =
                    androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(nombreEvento)
                Text(asistente.nombre)

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription =
                        "Entrada QR de ${asistente.nombre}",
                    modifier = Modifier.size(220.dp)
                )

                Text("Presenta este QR al ingresar.")
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        compartirEntrada(
                            context = context,
                            bitmap = bitmap,
                            asistente = asistente,
                            nombreEvento = nombreEvento
                        )
                    } catch (error: Exception) {
                        Toast.makeText(
                            context,
                            "No se pudo compartir la entrada",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                Text("Compartir")
            }
        },

        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text("Cerrar")
            }
        }
    )
}

private fun generarQr(contenido: String): Bitmap {
    val tamaño = 640

    val matriz = QRCodeWriter().encode(
        contenido,
        BarcodeFormat.QR_CODE,
        tamaño,
        tamaño
    )

    val pixeles = IntArray(tamaño * tamaño)

    for (y in 0 until tamaño) {
        for (x in 0 until tamaño) {
            pixeles[y * tamaño + x] = if (matriz[x, y]) {
                Color.BLACK
            } else {
                Color.WHITE
            }
        }
    }

    return Bitmap.createBitmap(
        pixeles,
        tamaño,
        tamaño,
        Bitmap.Config.ARGB_8888
    )
}

private fun compartirEntrada(
    context: Context,
    bitmap: Bitmap,
    asistente: Asistente,
    nombreEvento: String
) {
    val carpeta = File(context.cacheDir, "entradas")
    check(carpeta.exists() || carpeta.mkdirs())

    val archivo = File(carpeta, "entrada_${asistente.id}.png")

    archivo.outputStream().use { salida ->
        check(
            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                salida
            )
        )
    }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"

        putExtra(Intent.EXTRA_STREAM, uri)

        putExtra(
            Intent.EXTRA_TEXT,
            "Entrada para $nombreEvento — ${asistente.nombre}"
        )

        clipData = ClipData.newUri(
            context.contentResolver,
            "Entrada LunaPass",
            uri
        )

        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(intent, "Compartir entrada")
    )
}