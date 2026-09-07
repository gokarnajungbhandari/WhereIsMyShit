package com.example.whereismyshit.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.util.UUID

fun saveResizedImage(
    context: Context,
    sourceUri: Uri
): String? {

    val inputStream = context.contentResolver.openInputStream(sourceUri)
        ?: return null

    val originalBitmap = inputStream.use {
        BitmapFactory.decodeStream(it)
    } ?: return null

    val maxWidth = 1920
    val maxHeight = 1090

    val scale = minOf(
        maxWidth.toFloat() / originalBitmap.width,
        maxHeight.toFloat() / originalBitmap.height,
        1f
    )

    val newWidth =
        (originalBitmap.width * scale).toInt()

    val newHeight =
        (originalBitmap.height * scale).toInt()

    val resizedBitmap =
        if (
            newWidth != originalBitmap.width ||
            newHeight != originalBitmap.height
        ) {
            Bitmap.createScaledBitmap(
                originalBitmap,
                newWidth,
                newHeight,
                true
            )
        } else {
            originalBitmap
        }

    val imageDirectory = File(
        context.filesDir,
        "shit_images"
    )

    if (!imageDirectory.exists()) {
        imageDirectory.mkdirs()
    }

    val imageFile = File(
        imageDirectory,
        "${UUID.randomUUID()}.jpg"
    )

    imageFile.outputStream().use { outputStream ->
        resizedBitmap.compress(
            Bitmap.CompressFormat.JPEG,
            85,
            outputStream
        )
    }

    if (resizedBitmap !== originalBitmap) {
        resizedBitmap.recycle()
    }

    originalBitmap.recycle()

    return imageFile.absolutePath
}