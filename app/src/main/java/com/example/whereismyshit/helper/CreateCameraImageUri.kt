package com.example.whereismyshit.helper

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

fun createCameraImageUri(
    context: Context
): Pair<File, Uri> {

    val cameraDirectory = File(
        context.cacheDir,
        "camera_images"
    )

    cameraDirectory.mkdirs()

    val file = File(
        cameraDirectory,
        "${UUID.randomUUID()}.jpg"
    )

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    return file to uri
}