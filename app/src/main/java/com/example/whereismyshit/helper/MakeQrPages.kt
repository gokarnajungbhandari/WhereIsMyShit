package com.example.whereismyshit.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import androidx.print.PrintHelper
import com.example.whereismyshit.data.Shit
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId

fun getQrPdf(shits: Set<Shit>): PdfDocument {
    // create a new document
    val document = PdfDocument();

// create a page description
    val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create();

// start a page
    var page = document.startPage(pageInfo);

    // Get the Canvas belonging to this PDF page
    val canvas = page.canvas


    canvas.drawBitmap(
            getQrBitmaps(shits),
            75f,
            75f,
            null
    )


    // Finish page
    document.finishPage(page)

    // Write PDF to a file
    val outputStream = FileOutputStream("Qr Codes ${Instant.ofEpochMilli(System.currentTimeMillis())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()}.pdf")

    document.writeTo(outputStream)

    outputStream.close()

    // Close PDF
    document.close()
    return document
}

fun printBitmap(
    shits: Set<Shit>,
    context: Context
) {
    val printHelper = PrintHelper(context)

    printHelper.scaleMode =
        PrintHelper.SCALE_MODE_FIT

    val bitmap = Bitmap.createBitmap(
        2550,
        3300,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    canvas.drawBitmap(
        getQrBitmaps(shits),
        75f,
        75f,
        null
    )
    printHelper.printBitmap(
        "QR Code",
        bitmap
    )
}


fun getQrBitmaps(shits : Set<Shit>): Bitmap {
    val bitmap = Bitmap.createBitmap(
        2400,
        3150,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    var startX = 0f
    var startY = 0f
    var qrNo = 1
    // Drawing Qrs
    for (shit in shits){
        val qrBitMap = QR(shit).getQrBitmap() // defaulting the qrSize to 800

        canvas.drawBitmap(
            qrBitMap,
            startX,
            startY,
            null
        )

        if (qrNo % 3 == 0){
            startY += 800f
            startX = 0f
        } else{
            startX += 775
        }
        qrNo++
    }
    return bitmap
}

