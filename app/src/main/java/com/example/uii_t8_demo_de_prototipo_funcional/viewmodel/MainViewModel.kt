package com.example.uii_t8_demo_de_prototipo_funcional.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uii_t8_demo_de_prototipo_funcional.data.ItemRepository
import com.example.uii_t8_demo_de_prototipo_funcional.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ItemRepository()

    private val _items = mutableStateListOf<Item>()
    val items: List<Item> get() = _items

    init {
        refreshItems()
    }

    fun refreshItems() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                try {
                    repo.getItems()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList<Item>()
                }
            }
            _items.clear()
            _items.addAll(list)
        }
    }

    fun addItem(item: Item, imagePath: String? = null, onComplete: ((Long?) -> Unit)? = null) {
        viewModelScope.launch {
            val created = withContext(Dispatchers.IO) {
                repo.createItem(item, imagePath)
            }
            refreshItems()
            onComplete?.invoke(created?.id)
        }
    }

    fun updateItem(item: Item, imagePath: String? = null, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val updated = withContext(Dispatchers.IO) {
                repo.updateItem(item.id, item, imagePath)
            }
            refreshItems()
            onComplete?.invoke(updated != null)
        }
    }

    fun deleteItem(id: Long, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) {
                repo.deleteItem(id)
            }
            refreshItems()
            onComplete?.invoke(ok)
        }
    }

    fun getItemById(id: Long): Item? = items.find { it.id == id }

    fun login(username: String, password: String): Boolean {
        return username == "admin" && password == "123"
    }
}
