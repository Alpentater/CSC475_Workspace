package com.example.cta_7

//an interface that allows the conversion to be replaced with a dummy for when we test.
interface UnitConverter{
    fun convert(value: Double, category: ConversionCategory, fromUnit: String, toUnit: String): Double
}