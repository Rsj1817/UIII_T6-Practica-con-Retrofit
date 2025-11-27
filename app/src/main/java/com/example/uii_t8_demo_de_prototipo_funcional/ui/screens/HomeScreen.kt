package com.example.uii_t8_demo_de_prototipo_funcional.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uii_t8_demo_de_prototipo_funcional.model.Item
import com.example.uii_t8_demo_de_prototipo_funcional.ui.components.SearchBar
import com.example.uii_t8_demo_de_prototipo_funcional.viewmodel.MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import com.example.uii_t8_demo_de_prototipo_funcional.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onItemClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val items = viewModel.items

    val filteredItems = if (searchQuery.isBlank()) {
        items
    } else {
        items.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio") },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Encuentra a tu mejor amigo con nosotros",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.mascotas),
                contentDescription = "Mascotas",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { /* No se necesita acción adicional */ },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron elementos")
                }
            } else {
                LazyColumn {
                    items(filteredItems) { item ->
                        ItemCard(
                            item = item,
                            onClick = { onItemClick(item.id.toInt()) } // convertir Long -> Int para navegación
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Categoría: ${item.category}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
