package com.example.uii_t8_demo_de_prototipo_funcional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.uii_t8_demo_de_prototipo_funcional.ui.navigation.AppNavigation
import com.example.uii_t8_demo_de_prototipo_funcional.ui.theme.UII_T8_Demo_de_prototipo_funcionalTheme
import com.example.uii_t8_demo_de_prototipo_funcional.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UII_T8_Demo_de_prototipo_funcionalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    val viewModel: MainViewModel = viewModel()
    val navController = rememberNavController()

    // NO: viewModel.initializeExampleData()
    AppNavigation(navController = navController, viewModel = viewModel)
}
