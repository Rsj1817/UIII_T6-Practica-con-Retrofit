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
import androidx.compose.ui.text.input.KeyboardType
import com.example.uii_t8_demo_de_prototipo_funcional.R
import com.example.uii_t8_demo_de_prototipo_funcional.ui.components.*

@Composable
fun ForgotPasswordScreen(
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }

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
            text = "Recuperacion de Contraseña",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ingresa tu correo para recibir instrucciones",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Start
        )

        InputField(
            value = email,
            onValueChange = { email = it },
            label = "Ingresa tu correo",
            keyboardType = KeyboardType.Email,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        PrimaryButton(
            text = "Enviar enlace de recuperacion",
            onClick = onSubmitClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryButton(
            text = "Cancelar y regresar",
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}
