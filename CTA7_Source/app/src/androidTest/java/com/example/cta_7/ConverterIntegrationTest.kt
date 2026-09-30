package com.example.cta_7

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class ConverterIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() { hiltRule.inject() }

    @Test
    fun fullApplication_convertsCelsiusToFahrenheit() {
        composeRule.onNodeWithTag("input").performTextInput("100")

        composeRule.onNodeWithTag("convertButton").performClick()

        composeRule.onNodeWithTag("resultText").assertTextContains("212.00")
    }
}