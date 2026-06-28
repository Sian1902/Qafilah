package com.example.qafilah.features.catalog.presentation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CatalogScreen(modifier: Modifier = Modifier) {
    val vm: CatalogViewModel = viewModel()
    val state = vm.list.collectAsState()

    LazyColumn() {
        items(state.value.size) {
            Text(text = state.value[it].title)
        }
    }

}