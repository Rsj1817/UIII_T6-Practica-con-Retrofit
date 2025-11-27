package com.example.uii_t8_demo_de_prototipo_funcional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uii_t8_demo_de_prototipo_funcional.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: MainViewModel,
    itemId: Int,
    onEditClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val item = viewModel.getItemById(itemId.toLong())
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (item == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Elemento no encontrado")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles") },
                actions = {
                    IconButton(onClick = { onEditClick(itemId) }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = item.name, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))

                    Text(text = "Descripción:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
                    Text(text = item.description, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))

                    Text(text = "Categoría:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
                    Text(text = item.category, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este elemento?") },
            confirmButton = {
                TextButton(onClick = {
                    // Ejecutar delete en ViewModel (usa id Long)
                    viewModel.deleteItem(item.id) {
                        // opcional: manejar callback
                    }
                    showDeleteDialog = false
                    onDeleteClick()
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
