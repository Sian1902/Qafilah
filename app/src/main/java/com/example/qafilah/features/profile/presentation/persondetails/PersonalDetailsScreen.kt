package com.example.qafilah.features.profile.presentation.persondetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.ui_kit.components.profile.DetailTopBar
import com.example.ui_kit.components.profile.InfoField
import com.example.ui_kit.components.profile.ProfileAvatarWithLabel
import com.example.ui_kit.components.profile.SectionLabel
import com.example.ui_kit.theme.QafilahTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun PersonalDetailsScreen(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    viewModel: PersonalDetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPersonalDetails()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        DetailTopBar(
            title = "Personal Details",
            onBackClick = onBackClick,
            trailingIcon = Icons.Filled.Edit,
            onTrailingClick = onEditClick
        )

        when (val state = uiState) {
            is PersonalDetailsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is PersonalDetailsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadPersonalDetails() }) {
                        Text("Retry")
                    }
                }
            }

            is PersonalDetailsUiState.Success -> {
                PersonalDetailsContent(user = state.user)
            }
        }
    }
}

@Composable
private fun PersonalDetailsContent(user: AppUser) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ProfileAvatarWithLabel(
            name = user.fullName,
            label = "Member",
            modifier = Modifier.padding(top = 12.dp),
            avatarContent = {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )

        Spacer(Modifier.height(28.dp))

        SectionLabel(text = "Account Information", modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoField(label = "Full Name", value = user.fullName)
            InfoField(label = "Email Address", value = user.email ?: "N/A")
            InfoField(label = "Phone Number", value = user.phone ?: "N/A")
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonalDetailsScreenPreview() {
    QafilahTheme {
        PersonalDetailsContent(
            user = AppUser(
                id = "",
                email = "",
                firstName = "",
                lastName = "",
                phone = ""
            )
        )
    }
}
