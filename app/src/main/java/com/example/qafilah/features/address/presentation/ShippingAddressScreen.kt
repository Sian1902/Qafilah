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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.model.ShippingAddressesUiState
import com.example.qafilah.features.address.domain.model.toUiModel
import com.example.ui_kit.components.address.DashedAddButton
import com.example.ui_kit.components.address.GlassAddressCard
import com.example.ui_kit.components.address.QafilahTextField
import com.example.ui_kit.components.shared.QafilahConfirmationDialog
import kotlinx.coroutines.launch
import com.example.qafilah.features.address.domain.model.AddressSuggestion
import androidx.compose.ui.platform.LocalFocusManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressesScreen(
    uiState: ShippingAddressesUiState,
    isSearching: Boolean,
    suggestions: List<AddressSuggestion>,
    onAddressQueryChanged: (String) -> Unit,
    onClearSuggestions: () -> Unit,
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
    var waitingForSheetClose by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

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
            onConsumeOperationResult()
        }
    }

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
                        text = stringResource(R.string.address_shipping_addresses_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
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
                            defaultLabel = stringResource(R.string.address_default_label),
                            editContentDescription = stringResource(R.string.edit_cd),
                            deleteContentDescription = stringResource(R.string.address_delete_cd),
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
                            },
                            onDeleteClick = {
                                addressToDeleteId = it
                            },
                            onSetDefaultClick = {
                                onSetDefaultAddress(it)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        DashedAddButton(
                            text = stringResource(R.string.address_add_new_button),
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
                                text = stringResource(R.string.address_updating_label),
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
                    isSearching = isSearching,
                    suggestions = suggestions,
                    onAddressQueryChanged = onAddressQueryChanged,
                    onClearSuggestions = onClearSuggestions,
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
            title = stringResource(R.string.address_delete_button),
            message = stringResource(R.string.address_delete_confirm_message),
            yesButtonText = stringResource(R.string.dialog_confirm_remove),
            noButtonText = stringResource(R.string.dialog_dismiss_cancel),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressSheetContent(
    initialAddress: ShippingAddress?,
    isOperationInProgress: Boolean,
    isSearching: Boolean,
    suggestions: List<AddressSuggestion>,
    onAddressQueryChanged: (String) -> Unit,
    onClearSuggestions: () -> Unit,
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
    var phone by remember(initialAddress?.id) { mutableStateOf(initialAddress?.phone.orEmpty()) }
    var isDefault by remember(initialAddress?.id) { mutableStateOf(initialAddress?.isDefault ?: false) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var isSelectedFromApi by remember(initialAddress?.id) { mutableStateOf(initialAddress != null) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 48.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (initialAddress == null) stringResource(R.string.address_new_address_title) else stringResource(R.string.address_edit_address_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 1. The standard text field (no longer wrapped in a Box)
        QafilahTextField(
            label = stringResource(R.string.address_street_label),
            value = street,
            onValueChange = {
                street = it
                isSelectedFromApi = false
                onAddressQueryChanged(it)
                dropdownExpanded = true
            },
            placeholder = "e.g. 123 Tahrir St",
        )

        AnimatedVisibility(
            visible = dropdownExpanded && suggestions.isNotEmpty()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp) // Stops the list from taking over the screen
                    .padding(top = 4.dp, bottom = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn {
                    items(suggestions) { suggestion ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = suggestion.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                // Automatically populate form fields when item clicked
                                street = suggestion.street
                                city = suggestion.city
                                province = suggestion.province
                                country = suggestion.country
                                zipCode = suggestion.zipCode
                                isSelectedFromApi = true
                                dropdownExpanded = false
                                onClearSuggestions()
                                focusManager.clearFocus() // <-- This hides the keyboard instantly!
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QafilahTextField(label = stringResource(R.string.address_city_label), value = city, onValueChange = { city = it }, placeholder = "Cairo", modifier = Modifier.weight(1f),enabled = false)
            QafilahTextField(label = stringResource(R.string.address_province_label), value = province, onValueChange = { province = it }, placeholder = "Cairo", modifier = Modifier.weight(1f),enabled = false)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QafilahTextField(label = stringResource(R.string.address_country_label), value = country, onValueChange = { country = it }, placeholder = "Egypt", modifier = Modifier.weight(1f),enabled = false)
            QafilahTextField(label = stringResource(R.string.address_zip_label), value = zipCode, onValueChange = { zipCode = it }, placeholder = "11511", modifier = Modifier.weight(1f),enabled = false)
        }
        QafilahTextField(
            label = stringResource(R.string.phone_number),
            value = phone,
            onValueChange = { phone = it },
            placeholder = "+201XXXXXXXXX"
        )

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
                text = stringResource(R.string.address_set_default_label),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        val canSave = street.isNotBlank() && isSelectedFromApi && !isOperationInProgress

        Button(
            onClick = {
                val address = ShippingAddress(
                    id = initialAddress?.id ?: "",
                    label = if (initialAddress == null) "Address" else initialAddress.label,
                    icon = Icons.Default.Home,
                    street = street.trim(),
                    locationDetails = listOfNotNull(city.trim().ifBlank { null }, province.trim().ifBlank { null }, country.trim().ifBlank { null }, zipCode.trim().ifBlank { null }).joinToString(", "),
                    phone = phone.trim().ifBlank { null },
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
                    text = if (initialAddress == null) stringResource(R.string.address_save_button) else stringResource(R.string.address_update_button),
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
                Text(stringResource(R.string.address_delete_button))
            }
        }
    }
}
