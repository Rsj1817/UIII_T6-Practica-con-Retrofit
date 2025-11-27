package com.example.uii_t8_demo_de_prototipo_funcional.remote

data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: String? = null
)
