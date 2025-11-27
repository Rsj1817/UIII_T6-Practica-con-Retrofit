package com.example.uii_t8_demo_de_prototipo_funcional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.uii_t8_demo_de_prototipo_funcional.ui.components.*
import com.example.uii_t8_demo_de_prototipo_funcional.viewmodel.MainViewModel
import com.example.uii_t8_demo_de_prototipo_funcional.model.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetScreen(
    viewModel: MainViewModel,
    itemId: Int,
    onAcceptClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    // Convertimos a Long porque el ViewModel/DB usan Long
    val current = viewModel.getItemById(itemId.toLong())

    // Si aún no está cargado, mostramos valores vacíos hasta que llegue (no hay llamada async aquí)
    var petName by remember { mutableStateOf(current?.name ?: "") }
    var species by remember { mutableStateOf(current?.category ?: "") }
    // Intentamos descomponer description si tu app sigue el formato "Raza: ... | Género: ... | Edad: ... años"
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    // Si `current` existe y los campos aún vacíos los rellenamos (sólo la primera vez)
    LaunchedEffect(current) {
        current?.let { item ->
            petName = item.name
            species = item.category
            // intentar parsear description (opcional)
            val parts = item.description.split("|").map { it.trim() }
            parts.forEach { p ->
                when {
                    p.startsWith("Raza:", ignoreCase = true) -> breed = p.substringAfter(":").trim()
                    p.startsWith("Género:", ignoreCase = true) -> gender = p.substringAfter(":").trim()
                    p.startsWith("Edad:", ignoreCase = true) -> age = p.substringAfter(":").replace("años", "").trim()
                }
            }
        }
    }

    val speciesOptions = listOf("Perro", "Gato", "Ave", "Reptil", "Otro")
    val genderOptions = listOf("Macho", "Hembra", "Sin especificar")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Editar Mascota") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputField(
                value = petName,
                onValueChange = { petName = it },
                label = "Nombre de la mascota",
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            DropdownInput(
                selectedOption = species,
                options = speciesOptions,
                onOptionSelected = { species = it },
                label = "Especie",
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            InputField(
                value = breed,
                onValueChange = { breed = it },
                label = "Raza",
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            InputField(
                value = age,
                onValueChange = { age = it },
                label = "Edad (Años)",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            DropdownInput(
                selectedOption = gender,
                options = genderOptions,
                onOptionSelected = { gender = it },
                label = "Género",
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SecondaryButton(
                    text = "Cancelar",
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                PrimaryButton(
                    text = "Guardar Cambios",
                    onClick = {
                        // Armar descripción con el mismo formato que usas
                        val desc = "Raza: ${breed.ifBlank { "N/A" }} | Género: ${gender.ifBlank { "N/A" }} | Edad: ${if (age.isBlank()) "N/A" else "$age años"}"
                        val updated = Item(
                            id = itemId.toLong(),
                            name = petName.ifBlank { "Sin nombre" },
                            description = desc,
                            category = species.ifBlank { "Otro" }
                        )
                        viewModel.updateItem(updated) {
                            // opcional: manejar filas afectadas
                        }
                        onAcceptClick() // navega atrás / home
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
