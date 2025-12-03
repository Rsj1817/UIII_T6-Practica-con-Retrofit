package com.example.uii_t8_demo_de_prototipo_funcional.remote

import android.content.Context
import com.example.uii_t8_demo_de_prototipo_funcional.BuildConfig
import android.os.Build
import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    @Volatile
    private var retrofit: Retrofit? = null

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun init(context: Context) {
        val base = resolveBaseUrl(context)
        retrofit = Retrofit.Builder()
            .baseUrl(base)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        Log.i("RetrofitClient", "Usando BASE_URL: $base")
        if (!isEmulator()) discoverAndUpdate(context)
    }

    fun setBaseUrl(context: Context, url: String) {
        saveBaseUrl(context, url)
        init(context)
    }

    fun currentBaseUrl(context: Context): String = resolveBaseUrl(context)

    val api: ApiService
        get() = retrofit!!.create(ApiService::class.java)

    private fun resolveBaseUrl(context: Context): String {
        return if (isEmulator()) {
            "http://10.0.2.2:5000/"
        } else {
            val saved = getSavedBaseUrl(context)
            if (!saved.isNullOrBlank()) saved else (BuildConfig.BASE_URL.takeIf { it.isNotBlank() } ?: "http://10.0.0.0:5000/")
        }
    }

    private fun discoverAndUpdate(context: Context) {
        thread {
            try {
                val socket = DatagramSocket()
                socket.soTimeout = 1500
                val msg = "DISCOVER_RETROFIT_API".toByteArray()
                val broadcast = InetAddress.getByName("255.255.255.255")
                val packet = DatagramPacket(msg, msg.size, broadcast, 5001)
                socket.send(packet)

                val buf = ByteArray(1024)
                val resp = DatagramPacket(buf, buf.size)
                socket.receive(resp)
                val text = String(resp.data, 0, resp.length)
                if (text.startsWith("RETROFIT_API ")) {
                    val url = text.removePrefix("RETROFIT_API ").trim()
                    saveBaseUrl(context, url)
                    init(context)
                }
                socket.close()
            } catch (e: Exception) {
                // silencioso
            }
        }
    }

    private fun isEmulator(): Boolean {
        val fp = Build.FINGERPRINT
        val product = Build.PRODUCT
        val brand = Build.BRAND
        val device = Build.DEVICE
        return fp.contains("generic") || fp.contains("unknown") || product.contains("sdk") || product.contains("emulator") || brand.contains("generic") || device.contains("goldfish") || device.contains("ranchu")
    }

    private fun prefs(context: Context) = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private fun getSavedBaseUrl(context: Context): String? = prefs(context).getString("base_url", null)
    private fun saveBaseUrl(context: Context, url: String) { prefs(context).edit().putString("base_url", url).apply() }
}
