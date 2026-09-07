package com.example.whereismyshit.helper

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import android.graphics.Matrix

fun rotateBitmapIfNeeded(
    bitmap: Bitmap,
    orientation: Int
): Bitmap {

    val matrix = Matrix()

    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 ->
            matrix.postRotate(90f)

        ExifInterface.ORIENTATION_ROTATE_180 ->
            matrix.postRotate(180f)

        ExifInterface.ORIENTATION_ROTATE_270 ->
            matrix.postRotate(270f)

        else ->
            return bitmap
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
