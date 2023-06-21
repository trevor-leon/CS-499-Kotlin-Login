package com.example.kotlinlogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kotlinlogin.ui.theme.KotlinInventoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinInventoryTheme {
                // A surface container using the 'background' color from the theme
                LoginApp()
            }
        }
    }
}