package com.example.cta_7

//Each of the different conversions the application will support.
enum class ConversionCategory(val displayName: String, val units: List<String>){
    TEMPERATURE(
        displayName = "Temperature",
        units = listOf("Celsius", "Fahrenheit", "Kelvin"),
    ),
    LENGTH(
        displayName = "Length",
        units = listOf("Meters", "Kilometers", "Feet", "Miles"),
    ),
    WEIGHT(
        displayName = "Weight",
        units = listOf("Kilograms", "Grams", "Pounds", "Ounces"),
    )
}
