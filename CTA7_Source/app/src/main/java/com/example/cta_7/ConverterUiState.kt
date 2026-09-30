package com.example.cta_7

//Represents everything currently displayed on the converter screen.
data class ConverterUiState(
    val input: String = "",
    val category: ConversionCategory = ConversionCategory.TEMPERATURE,
    val fromUnit: String = "Celsius",
    val toUnit: String = "Fahrenheit",
    val result: String = "",
    val error: String? = null
)