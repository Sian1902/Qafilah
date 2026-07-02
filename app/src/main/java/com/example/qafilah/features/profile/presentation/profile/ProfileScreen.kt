package com.example.qafilah.features.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.profile.domain.model.CustomerProfile
import com.example.qafilah.features.profile.presentation.profile.ProfileUiState
import com.example.qafilah.features.profile.presentation.profile.ProfileViewModel
import com.example.ui_kit.components.profile.AccountMenuItem
import com.example.ui_kit.components.profile.ProfileHeaderCard
import com.example.ui_kit.components.profile.ProfileStatsRow
import com.example.ui_kit.components.profile.ProfileTopBar
import com.example.ui_kit.components.profile.SectionLabel
import com.example.ui_kit.components.profile.SignOutButton

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onEditProfileClick: () -> Unit = {},
    onPersonalDetailsClick: () -> Unit = {},
    onSavedPaymentsClick: () -> Unit = {},
    onShippingAddressesClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    when (val currentState = state) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ProfileUiState.Error -> {
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
                        text = currentState.message ?: "An error occurred",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (currentState.message?.contains("authenticated") == true) {
                        Button(onClick = onNavigateToLogin) {
                            Text("Go to Login")
                        }
                    } else {
                        Button(onClick = { viewModel.loadProfile() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

        is ProfileUiState.Success -> {
            ProfileScreenContent(
                profile = currentState.profile,
                onEditProfileClick = onEditProfileClick,
                onPersonalDetailsClick = onPersonalDetailsClick,
                onSavedPaymentsClick = onSavedPaymentsClick,
                onShippingAddressesClick = onShippingAddressesClick,
                onLanguageClick = onLanguageClick,
                onCurrencyClick = onCurrencyClick,
                onSignOutClick = onSignOutClick
            )
        }
    }
}

@Composable
private fun ProfileScreenContent(
    profile: CustomerProfile,

    onEditProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPersonalDetailsClick: () -> Unit = {},
    onSavedPaymentsClick: () -> Unit = {},
    onShippingAddressesClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ProfileTopBar(
                    greetingName = profile.user.fullName.substringBefore(" "),
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
                    name = profile.user.fullName,
                    email = profile.user.email ?: "No email",
                    onEditProfileClick = onEditProfileClick,
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
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(24.dp))
            }

            item {
                SectionLabel(text = "My Account", modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccountMenuItem(
                        icon = Icons.Filled.Person,
                        title = "Personal Details",
                        onClick = onPersonalDetailsClick
                    )
                    AccountMenuItem(
                        icon = Icons.Filled.CreditCard,
                        title = "Saved Payments",
                        subtitle = "Preference: Online Payment",
                        onClick = onSavedPaymentsClick
                    )
                    AccountMenuItem(
                        icon = Icons.Filled.LocationOn,
                        title = "Shipping Addresses",
                        subtitle = "${profile.addresses.size} saved locations",
                        onClick = onShippingAddressesClick
                    )
                }
                Spacer(Modifier.height(24.dp))
            }

            item {
                SectionLabel(text = "Preferences", modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccountMenuItem(
                        icon = Icons.Filled.Language,
                        title = "Language",
                        trailingText = "English",
                        onClick = onLanguageClick
                    )
                    AccountMenuItem(
                        icon = Icons.Filled.AttachMoney,
                        title = "Currency",
                        trailingText = "AED",
                        onClick = onCurrencyClick
                    )
                }
                Spacer(Modifier.height(28.dp))
            }

            item {
                SignOutButton(
                    onClick = onSignOutClick,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
