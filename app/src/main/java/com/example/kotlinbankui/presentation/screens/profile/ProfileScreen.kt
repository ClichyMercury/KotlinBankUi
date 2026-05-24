package com.example.kotlinbankui.presentation.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.example.kotlinbankui.data.network.dto.UserResponse
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimBottomBar
import com.example.kotlinbankui.presentation.components.finsim.FinSimTopBar
import com.example.kotlinbankui.presentation.components.finsim.LoadingScreen
import com.example.kotlinbankui.presentation.components.finsim.OrderButton
import com.example.kotlinbankui.presentation.components.finsim.OrderButtonStyle
import com.example.kotlinbankui.ui.theme.FinSimBlue
import com.example.kotlinbankui.ui.theme.FinSimBlueDark
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLogout()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = { FinSimTopBar(title = "Profil") },
        bottomBar = { FinSimBottomBar(navController) }
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(Modifier.padding(padding))

            state.user == null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                ErrorBanner(
                    message = state.errorMessage ?: "Profil indisponible",
                    onRetry = viewModel::refresh
                )
            }

            else -> ProfileContent(
                user = state.user!!,
                contentPadding = padding,
                onLogout = viewModel::logout
            )
        }
    }
}

@Composable
private fun ProfileContent(
    user: UserResponse,
    contentPadding: PaddingValues,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HeaderCard(user = user) }
        item { InfoCard(user = user) }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OrderButton(
                text = "Se déconnecter",
                onClick = onLogout,
                style = OrderButtonStyle.Neutral,
                leadingIcon = Icons.AutoMirrored.Filled.ExitToApp
            )
        }
    }
}

@Composable
private fun HeaderCard(user: UserResponse) {
    val initial = user.pseudo.firstOrNull()?.uppercase() ?: "?"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(FinSimBlue, FinSimBlueDark)),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "@${user.pseudo}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = "Membre depuis ${formatMemberSince(user)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun InfoCard(user: UserResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoLine(icon = Icons.Outlined.Person, label = "Pseudo", value = user.pseudo)
            InfoLine(icon = Icons.Outlined.Email, label = "Email", value = user.email)
        }
    }
}

@Composable
private fun InfoLine(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

private val memberDateFormatter = DateTimeFormatter
    .ofPattern("MMMM yyyy", Locale.FRENCH)
    .withZone(ZoneId.systemDefault())

private fun formatMemberSince(user: UserResponse): String =
    memberDateFormatter.format(user.createdAt).replaceFirstChar { it.uppercase() }
