package com.example.whereismyshit.helper

import com.example.whereismyshit.data.Shit
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class QR (val qrLink: String, val containerName: String){
    constructor(shit: Shit) : this("whereismyshit://container/${shit.id}", shit.name)

    fun getQrBitmap(sizePx: Int = 800): Bitmap {

        val borderPx = 2
        val textPx = sizePx / 8

        val finalWidth = sizePx - textPx
        val finalHeight = sizePx

        val qrSize = finalWidth - borderPx * 2

        val writer = QRCodeWriter()

        val bitMatrix = writer.encode(
            qrLink,
            BarcodeFormat.QR_CODE,
            qrSize,
            qrSize
        )

        val qrBitmap = Bitmap.createBitmap(
            qrSize,
            qrSize,
            Bitmap.Config.RGB_565
        )

        for (x in 0 until qrSize) {
            for (y in 0 until qrSize) {
                qrBitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) {
                        Color.BLACK
                    } else {
                        Color.WHITE
                    }
                )
            }
        }

        val finalBitmap = Bitmap.createBitmap(
            finalWidth,
            finalHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(finalBitmap)

        // Draw black outer border/background
        canvas.drawColor(Color.BLACK)

        val whitePaint = Paint().apply {
            color = Color.WHITE
        }

        // White inner area
        canvas.drawRect(
            borderPx.toFloat(),
            borderPx.toFloat(),
            (finalWidth - borderPx).toFloat(),
            (finalHeight - borderPx).toFloat(),
            whitePaint
        )

        // Center QR horizontally
        val qrX =
            (finalWidth - qrBitmap.width) / 2f

        canvas.drawBitmap(
            qrBitmap,
            qrX,
            borderPx.toFloat(),
            null
        )

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = sizePx / 20f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val textAreaTop =
            borderPx + qrBitmap.height.toFloat()

        val textAreaBottom =
            finalHeight - borderPx.toFloat()

        val fontMetrics =
            textPaint.fontMetrics

        val textBaseline =
            (textAreaTop + 2f)-
                    (fontMetrics.ascent + fontMetrics.descent) / 2f

        canvas.drawText(
            containerName,
            finalWidth / 2f,
            textBaseline,
            textPaint
        )

        return rotateBitMapLeftBy90(finalBitmap)
    }

    fun rotateBitMapLeftBy90(bitmap: Bitmap) : Bitmap{
        val matrix = Matrix().apply {
            postRotate(-90f)
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}

fun getContainerIdFromQr(value: String): Int? {

    val prefix =
        "whereismyshit://container/"

    if (!value.startsWith(prefix)) {
        return null
    }

    return value
        .removePrefix(prefix)
        .toIntOrNull()
}