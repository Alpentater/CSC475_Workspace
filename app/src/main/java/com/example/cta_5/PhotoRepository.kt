package com.example.cta_5

import android.content.Context
import android.os.Environment
import java.io.File

object PhotoRepository{
    private const val PHOTO_FOLDER = "ModernPhotoGallery"

    fun getPhotoDirectory(context: Context): File {
        val baseDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
        return File(baseDirectory, PHOTO_FOLDER).apply { if (!exists()) { mkdirs() } } }

    fun createPhotoFile(context: Context): File {
        val directory = getPhotoDirectory(context)
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"

        return File(directory, fileName)
    }

    fun loadPhotos(context:Context): List<File> {
        val directory = getPhotoDirectory(context)

        return directory.listFiles()?.filter {
            file -> file.isFile && (
            file.extension.equals("jpg", ignoreCase = true) || file.extension.equals("jpeg", ignoreCase = true))
        } ?.sortedByDescending {
            it.lastModified()
        } ?: emptyList()
    }
}