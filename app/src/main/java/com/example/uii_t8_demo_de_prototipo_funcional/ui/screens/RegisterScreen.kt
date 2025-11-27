package com.example.uii_t8_demo_de_prototipo_funcional.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uii_t8_demo_de_prototipo_funcional.R
import com.example.uii_t8_demo_de_prototipo_funcional.ui.components.*

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.icono),
            contentDescription = "User Icon",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )

        InputField(
            value = username,
            onValueChange = { username = it },
            label = "Nombre",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        PrimaryButton(
            text = "Registrarse",
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}
