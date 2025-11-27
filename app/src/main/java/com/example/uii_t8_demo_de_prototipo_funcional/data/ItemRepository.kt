package com.example.uii_t8_demo_de_prototipo_funcional.data

import com.example.uii_t8_demo_de_prototipo_funcional.model.Item
import com.example.uii_t8_demo_de_prototipo_funcional.remote.MultipartUtil
import com.example.uii_t8_demo_de_prototipo_funcional.remote.RetrofitClient
import okhttp3.MultipartBody
import java.io.File

class ItemRepository {

    private val api = RetrofitClient.api

    /** GET ALL */
    suspend fun getItems(): List<Item> {
        return try {
            api.getItems()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /** GET BY ID */
    suspend fun getItem(id: Long): Item? {
        return try {
            api.getItem(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** CREATE */
    suspend fun createItem(item: Item, imagePath: String?): Item? {
        return try {
            val name = MultipartUtil.createPartFromString(item.name ?: "")
            val desc = MultipartUtil.createPartFromString(item.description ?: "")
            val cat = MultipartUtil.createPartFromString(item.category ?: "")

            val imagePart: MultipartBody.Part? = imagePath?.let { path ->
                val file = File(path).takeIf { it.exists() }
                file?.let { MultipartUtil.prepareFilePart("image", it) }
            }

            api.createItem(name, desc, cat, imagePart)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** UPDATE */
    suspend fun updateItem(id: Long, item: Item, imagePath: String?): Item? {
        return try {
            val name = MultipartUtil.createPartFromString(item.name ?: "")
            val desc = MultipartUtil.createPartFromString(item.description ?: "")
            val cat = MultipartUtil.createPartFromString(item.category ?: "")

            val imagePart: MultipartBody.Part? = imagePath?.let { path ->
                val file = File(path).takeIf { it.exists() }
                file?.let { MultipartUtil.prepareFilePart("image", it) }
            }

            api.updateItem(id, name, desc, cat, imagePart)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** DELETE */
    suspend fun deleteItem(id: Long): Boolean {
        return try {
            api.deleteItem(id)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
