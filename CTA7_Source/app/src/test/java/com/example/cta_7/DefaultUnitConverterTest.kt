package com.example.cta_7


import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DefaultUnitConverterTest {
    private lateinit var converter: DefaultUnitConverter

    @Before
    fun setUp() {
        converter = DefaultUnitConverter()
    }

    @Test
    fun celsiusToFahrenheit_returns212() {
        val result = converter.convert(value = 100.0, category = ConversionCategory.TEMPERATURE, fromUnit = "Celsius", toUnit = "Fahrenheit")
        assertEquals(212.0, result, 0.001)
    }

    @Test
    fun fahrenheitToCelsius_returns0() {
        val result = converter.convert(value = 32.0, category = ConversionCategory.TEMPERATURE, fromUnit = "Fahrenheit", toUnit = "Celsius")
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun kilometersToMeters_returns1000() {
        val result = converter.convert(value = 1.0, category = ConversionCategory.LENGTH, fromUnit = "Kilometers", toUnit = "Meters")
        assertEquals(1000.0, result, 0.001)
    }

    @Test
    fun milesToKilometers_returnsCorrectValue() {
        val result = converter.convert(value = 1.0, category = ConversionCategory.LENGTH, fromUnit = "Miles", toUnit = "Kilometers")
        assertEquals(1.609344, result, 0.000001)
    }

    @Test
    fun poundsToKilograms_returnsCorrectValue() {
        val result = converter.convert(value = 1.0, category = ConversionCategory.WEIGHT, fromUnit = "Pounds", toUnit = "Kilograms")
        assertEquals(0.45359237, result, 0.000001)
    }
}