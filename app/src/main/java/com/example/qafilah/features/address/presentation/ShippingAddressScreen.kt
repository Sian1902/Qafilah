package com.example.qafilah.features.address.presentation


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.ui_kit.components.shared.QafilahConfirmationDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressesScreen(
    uiState: ShippingAddressesUiState,
    onBackClick: () -> Unit,
    onSaveNewAddress: (ShippingAddress) -> Unit,
    onEditAddress: (ShippingAddress) -> Unit,
    onDeleteAddress: (String) -> Unit,
    onSetDefaultAddress: (String) -> Unit,
    onConsumeOperationResult: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<ShippingAddress?>(null) }
    var addressToDeleteId by remember { mutableStateOf<String?>(null) }
    // Track whether we're waiting for an operation to complete to close the sheet
    var waitingForSheetClose by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Close the bottom sheet when an operation succeeds
    LaunchedEffect(uiState.operationSuccess) {
        if (uiState.operationSuccess == true && waitingForSheetClose) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheet = false
                    editingAddress = null
                }
            }
            waitingForSheetClose = false
            onConsumeOperationResult()
        } else if (uiState.operationSuccess == true) {
            // Operation succeeded but no sheet to close (e.g. set default from card)
            onConsumeOperationResult()
        }
    }

    // Show snackbar for errors
    LaunchedEffect(uiState.error) {
        val errorMsg = uiState.error
        if (errorMsg != null) {
            waitingForSheetClose = false
            snackbarHostState.showSnackbar(
                message = errorMsg,
                duration = SnackbarDuration.Short
            )
            onConsumeOperationResult()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && uiState.addresses.isEmpty()) {
                // Initial loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.addresses, key = { it.id }) { address ->
                        GlassAddressCard(
                            address = address.toUiModel(),
                            onClick = {
                                if (!address.isDefault && !uiState.isOperationInProgress) {
                                    onSetDefaultAddress(address.id)
                                }
                            },
                            onEditClick = {
                                if (!uiState.isOperationInProgress) {
                                    editingAddress = address
                                    showBottomSheet = true
                                }
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
                                if (!uiState.isOperationInProgress) {
                                    editingAddress = null
                                    showBottomSheet = true
                                }
                            }
                        )
                    }
                }
            }

            // Loading overlay for operations (set default, delete from list, etc.)
            AnimatedVisibility(
                visible = uiState.isOperationInProgress && !showBottomSheet,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Updating…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    if (!uiState.isOperationInProgress) {
                        showBottomSheet = false
                        editingAddress = null
                        waitingForSheetClose = false
                    }
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.12f)) }
            ) {
                AddAddressSheetContent(
                    initialAddress = editingAddress,
                    isOperationInProgress = uiState.isOperationInProgress,
                    onSaveClick = { address ->
                        waitingForSheetClose = true
                        if (editingAddress != null) {
                            onEditAddress(address)
                        } else {
                            onSaveNewAddress(address)
                        }
                    },
                    onDeleteClick = { addressId ->
                        addressToDeleteId = addressId
                    }
                )
            }
        }

        QafilahConfirmationDialog(
            isVisible = addressToDeleteId != null,
            title = "Delete address",
            message = "Are you sure you want to delete this address?",
            yesButtonText = "Delete",
            onYes = {
                addressToDeleteId?.let { id ->
                    waitingForSheetClose = true
                    onDeleteAddress(id)
                }
                addressToDeleteId = null
            },
            onNo = {
                addressToDeleteId = null
            }
        )
    }
}

@Composable
fun AddAddressSheetContent(
    initialAddress: ShippingAddress?,
    isOperationInProgress: Boolean,
    onSaveClick: (ShippingAddress) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val splitLocation = remember(initialAddress?.id) {
        initialAddress?.locationDetails
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            .orEmpty()
    }
    var street by remember(initialAddress?.id) { mutableStateOf(initialAddress?.street.orEmpty()) }
    var city by remember(initialAddress?.id) { mutableStateOf(splitLocation.getOrNull(0).orEmpty()) }
    var province by remember(initialAddress?.id) { mutableStateOf(splitLocation.getOrNull(1).orEmpty()) }
    var country by remember(initialAddress?.id) { mutableStateOf(splitLocation.getOrNull(2).orEmpty()) }
    var zipCode by remember(initialAddress?.id) { mutableStateOf(splitLocation.getOrNull(3).orEmpty()) }
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
                .clickable(enabled = !isOperationInProgress) { isDefault = !isDefault }
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

        val canSave = street.isNotBlank() && !isOperationInProgress

        Button(
            onClick = {
                val address = ShippingAddress(
                    id = initialAddress?.id ?: "",
                    label = if (initialAddress == null) "Address" else initialAddress.label,
                    icon = Icons.Default.Home,
                    street = street.trim(),
                    locationDetails = listOfNotNull(city.trim().ifBlank { null }, province.trim().ifBlank { null }, country.trim().ifBlank { null }, zipCode.trim().ifBlank { null }).joinToString(", "),
                    isDefault = isDefault
                )
                onSaveClick(address)
            },
            enabled = canSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(50)
        ) {
            if (isOperationInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.background
                )
            } else {
                Text(
                    text = if (initialAddress == null) "Save Address" else "Update Address",
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        if (initialAddress != null) {
            OutlinedButton(
                onClick = { onDeleteClick(initialAddress.id) },
                enabled = !isOperationInProgress,
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