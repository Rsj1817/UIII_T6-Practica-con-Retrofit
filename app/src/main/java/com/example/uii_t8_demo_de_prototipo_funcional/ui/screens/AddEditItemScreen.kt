package com.example.uii_t8_demo_de_prototipo_funcional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uii_t8_demo_de_prototipo_funcional.model.Item
import com.example.uii_t8_demo_de_prototipo_funcional.ui.components.*
import com.example.uii_t8_demo_de_prototipo_funcional.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    viewModel: MainViewModel,
    itemId: Int?,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val isEditMode = itemId != null
    val item = if (isEditMode) viewModel.getItemById(itemId!!.toLong()) else null

    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var category by remember { mutableStateOf(item?.category ?: "") }

    val categories = listOf("Tarea", "Proyecto", "Recordatorio", "Evento", "Otro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Elemento" else "Agregar Elemento") }
            )
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
                value = name,
                onValueChange = { name = it },
                label = "Nombre",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            InputField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            DropdownInput(
                selectedOption = category,
                options = categories,
                onOptionSelected = { category = it },
                label = "Categoría",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SecondaryButton(
                    text = "Cancelar",
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                PrimaryButton(
                    text = if (isEditMode) "Guardar Cambios" else "Agregar",
                    onClick = {
                        if (isEditMode) {
                            viewModel.updateItem(
                                Item(
                                    id = itemId!!.toLong(),
                                    name = name,
                                    description = description,
                                    category = category
                                )
                            )
                        } else {
                            viewModel.addItem(
                                Item(
                                    id = 0L, // id autogenerado
                                    name = name,
                                    description = description,
                                    category = category
                                )
                            )
                        }
                        onSaveClick()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
