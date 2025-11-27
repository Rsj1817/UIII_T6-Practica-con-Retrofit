package com.example.uii_t8_demo_de_prototipo_funcional.model
data class Item(
    val id: Long = 0L,
    val name: String,
    val description: String,
    val category: String,
    val imageUrl: String? = null
)
