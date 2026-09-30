package com.example.cta_7

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun UnitConverterScreen(viewModel: ConverterViewModel) {
    val state = viewModel.uiState
    UnitConverterContent(state = state, onInputChange = viewModel::updateInput, onCategoryChange = viewModel::updateCategory, onFromUnitChange = viewModel::updateFromUnit, onToUnitChange = viewModel::updateToUnit, onConvert = viewModel::convert)
}

//This composable is intentionally stateless.
//Separating the UI from the ViewModel makes Compose UI testing much easier.

//This is all just a bunch of hardcoded UI elements and I must admit... I hate it and AI was very helpful for this part.
//Sorry (not sorry) - Alex :)
@Composable
fun UnitConverterContent(state: ConverterUiState, onInputChange: (String) -> Unit, onCategoryChange: (ConversionCategory) -> Unit, onFromUnitChange: (String) -> Unit, onToUnitChange: (String) -> Unit, onConvert: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Unit Converter", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = state.input, onValueChange = onInputChange, label = { Text("Value") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth().testTag("input"))
        Text("Category")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ConversionCategory.entries) {
                category -> FilterChip(selected = state.category == category, onClick = { onCategoryChange(category) }, label = { Text(category.displayName)}, modifier = Modifier.testTag("category_${category.name}"))
            }
        }
        Text("From")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.category.units) { unit ->
                FilterChip(selected = state.fromUnit == unit, onClick = { onFromUnitChange(unit) }, label = { Text(unit) }, modifier = Modifier.testTag("from_$unit"))
            }
        }
        Text("To")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.category.units) {
                unit -> FilterChip(selected = state.toUnit == unit, onClick = { onToUnitChange(unit) }, label = { Text(unit) }, modifier = Modifier.testTag("to_$unit"))
            }
        }
        Button(onClick = onConvert, modifier = Modifier.fillMaxWidth().testTag("convertButton")) { Text("Convert") }
        if (state.result.isNotEmpty()) {
            Text(text = "Result: ${state.result}", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.testTag("resultText"))
        }
        state.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("errorText"))
        }
    }
}