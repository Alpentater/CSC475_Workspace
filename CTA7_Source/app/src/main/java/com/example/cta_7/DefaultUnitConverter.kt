package com.example.cta_7

import javax.inject.Inject
import javax.inject.Singleton

//This is where we will do the actual mathematical conversions
//The important thing to note here is that this logic is seperate from the UI
//That way, it is much easier to test with Junit
@Singleton
class DefaultUnitConverter @Inject constructor():
        UnitConverter{
            override fun convert(value: Double, category: ConversionCategory, fromUnit: String, toUnit: String): Double {
                return when (category) {
                    ConversionCategory.TEMPERATURE -> convertTemperature(value, fromUnit, toUnit)
                    ConversionCategory.LENGTH -> convertLength(value, fromUnit, toUnit)
                    ConversionCategory.WEIGHT -> convertWeight(value, fromUnit, toUnit)
                }
            }

            private fun convertTemperature(value: Double, from: String, to: String): Double {
                //Converting the input into celsius first
                val celsius = when(from){
                    "Celsius" -> value
                    "Fahrenheit" -> (value - 32.0) * 5.0 / 9.0
                    "Kelvin" -> value - 273.15
                    else -> throw IllegalArgumentException("Unsupported temperature unit")
                }

                //Converting celsius into the requested unit. (It is much easier to do it this way... mathematically)
                return when(to){
                    "Celsius" -> celsius
                    "Fahrenheit" -> celsius * 9.0 / 5.0 + 32.0
                    "Kelvin" -> celsius + 273.15
                    else -> throw IllegalArgumentException("Unsupported temperature unit")
                }
            }
            private fun convertLength(value: Double, from: String, to: String): Double {
                //This map will represent how many meters are within each unit.
                val meterFactors = mapOf("Meters" to 1.0, "Kilometers" to 1000.0, "Feet" to 0.3048, "Miles" to 1609.344)
                val fromFactor = meterFactors[from]?: throw IllegalArgumentException("Unsupported length unit")
                val toFactor = meterFactors[to]?: throw IllegalArgumentException("Unsupported length unit")
                val meters = value*fromFactor
                return meters/toFactor
            }
            private fun convertWeight(value: Double, from: String, to: String): Double {
                //Doing the same thing as the previous convert functions, but basing it on kilograms instead.
                val kilogramFactors = mapOf("Kilograms" to 1.0, "Grams" to 0.001, "Pounds" to 0.45359237, "Ounces" to 0.028349523125)
                val fromFactor = kilogramFactors[from]?: throw IllegalArgumentException("Unsupported weight unit")
                val toFactor = kilogramFactors[to]?: throw IllegalArgumentException("Unsupported weight unit")
                val kilograms = value*fromFactor
                return kilograms/toFactor
            }
}

