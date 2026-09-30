package com.example.cta_7

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class UnitConverterScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun enterValueAndConvert_displaysResult() {

        var state by mutableStateOf(ConverterUiState())

        composeRule.setContent {
            UnitConverterContent(state = state, onInputChange = { state = state.copy(input = it) }, onCategoryChange = {}, onFromUnitChange = {}, onToUnitChange = {}, onConvert = { state = state.copy(result = "212.00") })
        }

        composeRule.onNodeWithTag("input").performTextInput("100")

        composeRule.onNodeWithTag("convertButton").performClick()

        composeRule.onNodeWithTag("resultText").assertIsDisplayed().assertTextContains("212.00")
    }

    @Test
    fun categorySelection_changesSelectedCategory() {

        var state by mutableStateOf(ConverterUiState())

        composeRule.setContent {
            UnitConverterContent(state = state, onInputChange = {}, onCategoryChange = { state = state.copy(category = it, fromUnit = it.units[0], toUnit = it.units[1]) }, onFromUnitChange = {}, onToUnitChange = {}, onConvert = {})
        }

        composeRule.onNodeWithTag("category_LENGTH").performClick()

        composeRule.onNodeWithTag("category_LENGTH").assertIsSelected()
    }
}