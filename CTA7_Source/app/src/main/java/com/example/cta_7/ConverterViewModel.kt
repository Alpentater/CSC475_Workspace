package com.example.cta_7

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

//Hilt creates the ViewModel and automatically supplies the UnitConverter dependency.
 @HiltViewModel
class ConverterViewModel @Inject constructor(private val unitConverter: UnitConverter): ViewModel() {
    var uiState by mutableStateOf(ConverterUiState())
        private set

    fun updateInput(input: String) {
        uiState = uiState.copy(input = input, error = null)
    }

    fun updateCategory(category: ConversionCategory) {
        val units = category.units
        uiState = uiState.copy(
            category = category,
            fromUnit = units[0],
            toUnit = units[1],
            result = "",
            error = null
        )
    }

    fun updateFromUnit(unit: String) {
        uiState = uiState.copy(fromUnit = unit, result = "")
    }

    fun updateToUnit(unit: String) {
        uiState = uiState.copy(toUnit = unit, result = "")
    }

    fun convert() {
        val number = uiState.input.toDoubleOrNull()
        if (number == null) {
            uiState = uiState.copy(result = "", error = "Enter a valid number.")
            return
        }
        val converted = unitConverter.convert(
            value = number,
            category = uiState.category,
            fromUnit = uiState.fromUnit,
            toUnit = uiState.toUnit
        )
        uiState = uiState.copy(result = String.format(Locale.US, "%.2f", converted), error = null)
    }
}
