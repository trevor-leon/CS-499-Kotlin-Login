package com.example.kotlinlogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kotlinlogin.ui.login.LoginScreen
import com.example.kotlinlogin.ui.theme.KotlinInventoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinInventoryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    InventoryApp()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginActivity() {
    KotlinInventoryTheme {
        // TODO: start Login Activity, I think ***
        LoginScreen()
    }
}
