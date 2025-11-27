package com.example.uii_t8_demo_de_prototipo_funcional.remote

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object MultipartUtil {

    /** Convierte un String a RequestBody (text/plain) */
    fun createPartFromString(value: String): RequestBody =
        value.toRequestBody("text/plain".toMediaTypeOrNull())

    /** Prepara un archivo como MultipartBody.Part para envío multipart */
    fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}
