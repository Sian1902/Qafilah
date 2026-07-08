package com.example.qafilah.features.profile.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qafilah.MainViewModel
import com.example.qafilah.R
import com.example.qafilah.core.preferences.ThemeMode
import com.example.qafilah.core.currency.domain.model.CurrencyMetadata
import com.example.qafilah.features.profile.domain.model.CustomerProfile
import com.example.ui_kit.components.profile.AccountMenuItem
import com.example.ui_kit.components.profile.ProfileHeaderCard
import com.example.ui_kit.components.profile.ProfileStatsRow
import com.example.ui_kit.components.profile.ProfileTopBar
import com.example.ui_kit.components.profile.SectionLabel
import com.example.ui_kit.components.profile.SignOutButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    mainViewModel: MainViewModel = koinViewModel(),
    onEditProfileClick: () -> Unit = {},
    onPersonalDetailsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onShippingAddressesClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val appState by mainViewModel.appState.collectAsStateWithLifecycle()

    var showCurrencyPicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }

    val unknownErrorMessage = stringResource(R.string.error_unknown)
    val genericErrorFallback = stringResource(R.string.error_occurred)

    LaunchedEffect(Unit) {
        viewModel.loadProfile(unknownErrorMessage)
    }

    if (showCurrencyPicker) {
        CurrencyPicker(
            selectedCurrency = uiState.selectedCurrency,
            availableCurrencies = uiState.availableCurrencies,
            onCurrencySelected = {
                viewModel.onCurrencySelected(it)
                showCurrencyPicker = false
            },
            onDismiss = { showCurrencyPicker = false }
        )
    }

    if (showLanguagePicker) {
        LanguagePicker(
            selectedLanguage = appState.languageCode,
            onLanguageSelected = {
                mainViewModel.setLanguage(it)
                showLanguagePicker = false
            },
            onDismiss = { showLanguagePicker = false }
        )
    }

    if (showThemePicker) {
        ThemePicker(
            selectedTheme = appState.themeMode,
            onThemeSelected = {
                mainViewModel.setTheme(it)
                showThemePicker = false
            },
            onDismiss = { showThemePicker = false }
        )
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = uiState.error ?: genericErrorFallback,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (uiState.isAuthError) {
                        Button(onClick = onNavigateToLogin) {
                            Text(stringResource(R.string.go_to_login))
                        }
                    } else {
                        Button(onClick = {
                            viewModel.loadProfile(unknownErrorMessage)
                        }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
        }

        uiState.profile != null -> {
            ProfileScreenContent(
                profile = uiState.profile!!,
                selectedCurrency = uiState.selectedCurrency,
                selectedLanguage = if (appState.languageCode == "ar") stringResource(R.string.language_arabic) else stringResource(R.string.language_english),
                selectedTheme = when (appState.themeMode) {
                    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                    ThemeMode.DARK -> stringResource(R.string.theme_dark)
                    ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                },
                onEditProfileClick = onEditProfileClick,
                onPersonalDetailsClick = onPersonalDetailsClick,
                onOrdersClick = onOrdersClick,
                onShippingAddressesClick = onShippingAddressesClick,
                onLanguageClick = { showLanguagePicker = true },
                onThemeClick = { showThemePicker = true },
                onCurrencyClick = { showCurrencyPicker = true },
                onSignOutClick = onSignOutClick
            )
        }
    }
}

@Composable
private fun ProfileScreenContent(
    profile: CustomerProfile,
    selectedCurrency: String,
    selectedLanguage: String,
    selectedTheme: String,
    onEditProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPersonalDetailsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onShippingAddressesClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom =  120.dp),
        ) {
            item {
                ProfileTopBar(
                    greetingText = stringResource(
                        R.string.welcome_user_greeting,
                        profile.user.fullName.substringBefore(" ")
                    ),
                    notificationsContentDescription = stringResource(R.string.welcome_notifications_cd),
                    onNotificationsClick = onNotificationsClick,
                    trailingAvatar = {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                )
            }

            item {
                ProfileHeaderCard(
                    name = profile.user.fullName.ifBlank { stringResource(R.string.default_username) },
                    email = profile.user.email ?: stringResource(R.string.no_email),
                    onEditProfileClick = onEditProfileClick,
                    verifiedContentDescription = stringResource(R.string.profile_verified_cd),
                    editProfileLabel = stringResource(R.string.profile_edit_profile),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    avatarContent = {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                )
                Spacer(Modifier.height(20.dp))
            }

            item {
                ProfileStatsRow(
                    orders = profile.orders.size,
                    wishlist = 0,
                    reviews = 0,
                    ordersLabel = stringResource(R.string.profile_stats_orders),
                    wishlistLabel = stringResource(R.string.profile_stats_wishlist),
                    reviewsLabel = stringResource(R.string.profile_stats_reviews),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(24.dp))
            }

            item {
                SectionLabel(
                    text = stringResource(R.string.my_account),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccountMenuItem(
                        icon = Icons.Filled.Person,
                        title = stringResource(R.string.personal_details),
                        iconContentDescription = stringResource(R.string.personal_details_icon_cd),
                        onClick = onPersonalDetailsClick
                    )
                    AccountMenuItem(
                        icon = Icons.Filled.ShoppingBag,
                        title = stringResource(R.string.my_orders),
                        subtitle = stringResource(
                            R.string.orders_count,
                            profile.orders.size
                        ),
                        iconContentDescription = stringResource(R.string.orders_icon_cd),
                        onClick = onOrdersClick
                    )
                    AccountMenuItem(
                        icon = Icons.Filled.LocationOn,
                        title = stringResource(R.string.shipping_addresses),
                        subtitle = stringResource(
                            R.string.saved_locations_count,
                            profile.addresses.size
                        ),
                        iconContentDescription = stringResource(R.string.shipping_icon_cd),
                        onClick = onShippingAddressesClick
                    )
                }
                Spacer(Modifier.height(24.dp))
            }

            item {
                SectionLabel(
                    text = stringResource(R.string.preferences),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccountMenuItem(
                        icon = Icons.Filled.Language,
                        title = stringResource(R.string.language),
                        trailingText = selectedLanguage,
                        iconContentDescription = stringResource(R.string.language_icon_cd),
                        onClick = onLanguageClick
                    )
                    AccountMenuItem(
                        icon = Icons.Filled.AttachMoney,
                        title = stringResource(R.string.currency),
                        trailingText = selectedCurrency,
                        iconContentDescription = stringResource(R.string.currency_icon_cd),
                        onClick = onCurrencyClick
                    )
                    AccountMenuItem(
                        icon = Icons.Filled.Check,
                        title = stringResource(R.string.theme_label),
                        trailingText = selectedTheme,
                        onClick = onThemeClick
                    )
                }
                Spacer(Modifier.height(28.dp))
            }

            item {
                SignOutButton(
                    label = stringResource(R.string.profile_sign_out),
                    iconContentDescription = stringResource(R.string.profile_sign_out_cd),
                    onClick = onSignOutClick,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CurrencyPicker(
    selectedCurrency: String,
    availableCurrencies: List<CurrencyMetadata>,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.currency)) },
        text = {
            if (availableCurrencies.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            } else {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(availableCurrencies) { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCurrencySelected(currency.code) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currency.code,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = currency.fullName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            if (currency.code == selectedCurrency) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.go_back))
            }
        }
    )
}

@Composable
fun LanguagePicker(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf("en" to stringResource(R.string.language_english), "ar" to stringResource(R.string.language_arabic))
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language_picker_title)) },
        text = {
            Column {
                languages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = name, style = MaterialTheme.typography.bodyLarge)
                        if (code == selectedLanguage) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.go_back))
            }
        }
    )
}

@Composable
fun ThemePicker(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf(
        ThemeMode.SYSTEM to stringResource(R.string.theme_system),
        ThemeMode.LIGHT to stringResource(R.string.theme_light),
        ThemeMode.DARK to stringResource(R.string.theme_dark)
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.theme_label)) },
        text = {
            Column {
                themes.forEach { (mode, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(mode) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = name, style = MaterialTheme.typography.bodyLarge)
                        if (mode == selectedTheme) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.go_back))
            }
        }
    )
}
