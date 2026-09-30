package com.example.cta_7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import dagger.hilt.android.AndroidEntryPoint
//import com.example.cta_7.ui.theme.CTA_7Theme
//@AndroidEntryPoint allows Hilt to inject Android framework classes such as this Activity.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //Hilt automatically creates this ViewModel and injects DefaultUnitConverter into it.
    private val converterViewModel: ConverterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    UnitConverterScreen(viewModel = converterViewModel)
                }
            }
        }
    }
}
