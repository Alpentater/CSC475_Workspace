package com.example.cta_7

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ConverterViewModelTest {

    private lateinit var converter: UnitConverter

    private lateinit var viewModel: ConverterViewModel

    @Before
    fun setUp() {
        converter = mockk()
        viewModel = ConverterViewModel(converter)
    }

    @Test
    fun convert_validInput_updatesResult() {
        every {
            converter.convert(100.0, ConversionCategory.TEMPERATURE, "Celsius", "Fahrenheit")
        } returns 212.0
        viewModel.updateInput("100")
        viewModel.convert()

        assertEquals("212.00", viewModel.uiState.result)

        assertNull(viewModel.uiState.error)

        verify(exactly = 1) {
            converter.convert(100.0, ConversionCategory.TEMPERATURE, "Celsius", "Fahrenheit")
        }
    }

    @Test
    fun convert_invalidInput_displaysError() {

        viewModel.updateInput("abc")

        viewModel.convert()

        assertEquals("", viewModel.uiState.result)

        assertEquals("Enter a valid number.", viewModel.uiState.error)

        verify(exactly = 0) {
            converter.convert(any(), any(), any(), any())
        }
    }

    @Test
    fun changingCategory_updatesDefaultUnits() {
        viewModel.updateCategory(ConversionCategory.LENGTH)

        assertEquals(ConversionCategory.LENGTH, viewModel.uiState.category)

        assertEquals("Meters", viewModel.uiState.fromUnit)

        assertEquals("Kilometers", viewModel.uiState.toUnit)
    }
}