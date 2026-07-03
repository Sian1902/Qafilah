package com.example.qafilah.features.address.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.model.ShippingAddressesUiState
import com.example.qafilah.features.address.domain.model.toUiModel
import com.example.ui_kit.components.address.DashedAddButton
import com.example.ui_kit.components.address.GlassAddressCard
import com.example.ui_kit.components.address.QafilahTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressesScreen(
    uiState: ShippingAddressesUiState,
    onBackClick: () -> Unit,
    onSaveNewAddress: (ShippingAddress) -> Unit,
    onEditAddress: (ShippingAddress) -> Unit,
    onDeleteAddress: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<ShippingAddress?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Shipping Addresses",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.addresses) { address ->
                GlassAddressCard(
                    address = address.toUiModel(),
                    onClick = { /* Handle Selection */ },
                    onEditClick = {
                        editingAddress = address
                        showBottomSheet = true
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                DashedAddButton(
                    text = "Add New Address",
                    icon = {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    onClick = {
                        editingAddress = null
                        showBottomSheet = true
                    }
                )
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.12f)) }
            ) {
                AddAddressSheetContent(
                    initialAddress = editingAddress,
                    onSaveClick = { address ->
                        if (editingAddress != null) {
                            onEditAddress(address)
                        } else {
                            onSaveNewAddress(address)
                        }
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                editingAddress = null
                            }
                        }
                    },
                    onDeleteClick = { addressId ->
                        onDeleteAddress(addressId)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                editingAddress = null
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AddAddressSheetContent(
    initialAddress: ShippingAddress?,
    onSaveClick: (ShippingAddress) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    var street by remember(initialAddress?.id) { mutableStateOf(initialAddress?.street.orEmpty()) }
    var city by remember(initialAddress?.id) { mutableStateOf(initialAddress?.locationDetails.orEmpty()) }
    var province by remember(initialAddress?.id) { mutableStateOf("") }
    var country by remember(initialAddress?.id) { mutableStateOf("") }
    var zipCode by remember(initialAddress?.id) { mutableStateOf("") }
    var isDefault by remember(initialAddress?.id) { mutableStateOf(initialAddress?.isDefault ?: false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 48.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (initialAddress == null) "New Address" else "Edit Address",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        QafilahTextField(label = "Street Address", value = street, onValueChange = { street = it }, placeholder = "e.g. 123 Tahrir St")

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QafilahTextField(label = "City", value = city, onValueChange = { city = it }, placeholder = "Cairo", modifier = Modifier.weight(1f))
            QafilahTextField(label = "Province", value = province, onValueChange = { province = it }, placeholder = "Cairo", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QafilahTextField(label = "Country", value = country, onValueChange = { country = it }, placeholder = "Egypt", modifier = Modifier.weight(1f))
            QafilahTextField(label = "Zip Code", value = zipCode, onValueChange = { zipCode = it }, placeholder = "11511", modifier = Modifier.weight(1f))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable { isDefault = !isDefault }
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isDefault) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (isDefault) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDefault) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.background, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Set as default shipping address",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        Button(
            onClick = {
                val address = ShippingAddress(
                    id = initialAddress?.id ?: "",
                    label = if (initialAddress == null) "Home" else initialAddress.label,
                    icon = Icons.Default.Home,
                    street = street.trim(),
                    locationDetails = listOfNotNull(city.trim().ifBlank { null }, province.trim().ifBlank { null }, country.trim().ifBlank { null }, zipCode.trim().ifBlank { null }).joinToString(", "),
                    isDefault = isDefault
                )
                onSaveClick(address)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = if (initialAddress == null) "Save Address" else "Update Address",
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (initialAddress != null) {
            OutlinedButton(
                onClick = { onDeleteClick(initialAddress.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete Address")
            }
        }
    }
}