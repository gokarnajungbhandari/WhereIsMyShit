package com.example.whereismyshit.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.util.UUID

fun saveResizedImage(
    context: Context,
    sourceUri: Uri
): String? {

    val orientationStream =
        context.contentResolver.openInputStream(sourceUri)
            ?: return null

    val exif = orientationStream.use {
        ExifInterface(it)
    }

    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    /*val inputStream = context.contentResolver.openInputStream(sourceUri)
        ?: return null*/

    val bitmapStream =
        context.contentResolver.openInputStream(sourceUri)
            ?: return null

    val originalBitmap = bitmapStream.use {
        BitmapFactory.decodeStream(it)
    } ?: return null

    val maxWidth = 1920
    val maxHeight = 1090

    val rotatedBitmap =
        rotateBitmapIfNeeded(
            originalBitmap,
            orientation
        )



    val scale = minOf(
        maxWidth.toFloat() / rotatedBitmap.width,
        maxHeight.toFloat() / rotatedBitmap.height,
        1f
    )

    val newWidth =
        (rotatedBitmap.width * scale).toInt()

    val newHeight =
        (rotatedBitmap.height * scale).toInt()

    val resizedBitmap =
        if (
            newWidth != rotatedBitmap.width ||
            newHeight != rotatedBitmap.height
        ) {
            Bitmap.createScaledBitmap(
                rotatedBitmap,
                newWidth,
                newHeight,
                true
            )
        } else {
            rotatedBitmap
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