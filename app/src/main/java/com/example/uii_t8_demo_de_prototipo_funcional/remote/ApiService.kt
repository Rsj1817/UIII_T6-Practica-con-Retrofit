package com.example.uii_t8_demo_de_prototipo_funcional.remote

import androidx.compose.ui.graphics.vector.Path
import com.example.uii_t8_demo_de_prototipo_funcional.model.Item
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @GET("items")
    suspend fun getItems(): List<Item>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: Long): Item

    @Multipart
    @POST("items")
    suspend fun createItem(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part image: MultipartBody.Part?
    ): Item

    @Multipart
    @PUT("items/{id}")
    suspend fun updateItem(
        @Path("id") id: Long,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part image: MultipartBody.Part?
    ): Item

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: Long)
}
