package com.example.kotlinbankui.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.components.BottomNavigationBar

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
    val accountNumber: String,
    val memberSince: String,
    val accountType: String,
    val profileImageUrl: String? = null
)

data class ProfileMenuItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val iconColor: Color = Color.Gray,
    val hasSwitch: Boolean = false,
    val switchState: Boolean = false,
    val showBadge: Boolean = false,
    val badgeText: String? = null,
    val onClick: () -> Unit = {}
)

data class ProfileSection(
    val title: String,
    val items: List<ProfileMenuItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    val userProfile = UserProfile(
        name = "John Doe",
        email = "john.doe@email.com",
        phone = "+1 (555) 123-4567",
        accountNumber = "****5154",
        memberSince = "March 2020",
        accountType = "Premium Account"
    )

    val profileSections = listOf(
        ProfileSection(
            title = "Account Settings",
            items = listOf(
                ProfileMenuItem(
                    id = "personal_info",
                    title = "Personal Information",
                    subtitle = "Update your personal details",
                    icon = Icons.Default.Person,
                    iconColor = Color(0xFF2196F3)
                ),
                ProfileMenuItem(
                    id = "security",
                    title = "Security & Privacy",
                    subtitle = "Password, 2FA, biometric",
                    icon = Icons.Default.Security,
                    iconColor = Color(0xFFF44336)
                ),
                ProfileMenuItem(
                    id = "cards",
                    title = "Cards & Accounts",
                    subtitle = "Manage your cards and accounts",
                    icon = Icons.Default.CreditCard,
                    iconColor = Color(0xFF9C27B0)
                ),
                ProfileMenuItem(
                    id = "limits",
                    title = "Transaction Limits",
                    subtitle = "Set spending and transfer limits",
                    icon = Icons.Default.AccountBalance,
                    iconColor = Color(0xFF4CAF50)
                )
            )
        ),
        ProfileSection(
            title = "Preferences",
            items = listOf(
                ProfileMenuItem(
                    id = "notifications",
                    title = "Notifications",
                    subtitle = "Push notifications, emails, SMS",
                    icon = Icons.Default.Notifications,
                    iconColor = Color(0xFFFF9800),
                    hasSwitch = true,
                    switchState = notificationsEnabled
                ),
                ProfileMenuItem(
                    id = "biometric",
                    title = "Biometric Login",
                    subtitle = "Use fingerprint or face ID",
                    icon = Icons.Default.Fingerprint,
                    iconColor = Color(0xFF00BCD4),
                    hasSwitch = true,
                    switchState = biometricEnabled
                ),
                ProfileMenuItem(
                    id = "theme",
                    title = "Dark Mode",
                    subtitle = "Switch between light and dark theme",
                    icon = Icons.Default.DarkMode,
                    iconColor = Color(0xFF607D8B),
                    hasSwitch = true,
                    switchState = darkModeEnabled
                ),
                ProfileMenuItem(
                    id = "language",
                    title = "Language",
                    subtitle = "English (US)",
                    icon = Icons.Default.Language,
                    iconColor = Color(0xFF795548)
                )
            )
        ),
        ProfileSection(
            title = "Support & Legal",
            items = listOf(
                ProfileMenuItem(
                    id = "help",
                    title = "Help Center",
                    subtitle = "FAQs, contact support",
                    icon = Icons.Default.Help,
                    iconColor = Color(0xFF3F51B5)
                ),
                ProfileMenuItem(
                    id = "feedback",
                    title = "Send Feedback",
                    subtitle = "Help us improve the app",
                    icon = Icons.Default.Feedback,
                    iconColor = Color(0xFFE91E63)
                ),
                ProfileMenuItem(
                    id = "privacy",
                    title = "Privacy Policy",
                    subtitle = "How we handle your data",
                    icon = Icons.Default.PrivacyTip,
                    iconColor = Color(0xFF009688)
                ),
                ProfileMenuItem(
                    id = "terms",
                    title = "Terms of Service",
                    subtitle = "Legal terms and conditions",
                    icon = Icons.Default.Gavel,
                    iconColor = Color(0xFF8BC34A)
                )
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edit profile */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(userProfile = userProfile)
            }

            // Quick Stats
            item {
                QuickStatsCard()
            }

            // Profile Sections
            items(profileSections) { section ->
                ProfileSectionCard(
                    section = section,
                    onNotificationToggle = { notificationsEnabled = !notificationsEnabled },
                    onBiometricToggle = { biometricEnabled = !biometricEnabled },
                    onThemeToggle = { darkModeEnabled = !darkModeEnabled }
                )
            }

            // Logout Button
            item {
                LogoutSection()
            }

            // App Version
            item {
                AppVersionSection()
            }
        }
    }
}

@Composable
fun ProfileHeader(userProfile: UserProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userProfile.name.split(" ").map { it.first() }.joinToString(""),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info
            Text(
                text = userProfile.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = userProfile.email,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Account Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem("Account", userProfile.accountNumber)
                InfoItem("Type", userProfile.accountType)
                InfoItem("Member Since", userProfile.memberSince)
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun QuickStatsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickStatItem(
                icon = Icons.Default.CreditCard,
                count = "3",
                label = "Active Cards",
                color = Color(0xFF2196F3)
            )

            QuickStatItem(
                icon = Icons.Default.AccountBalance,
                count = "2",
                label = "Accounts",
                color = Color(0xFF4CAF50)
            )

            QuickStatItem(
                icon = Icons.Default.TrendingUp,
                count = "127",
                label = "Transactions",
                color = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
fun QuickStatItem(
    icon: ImageVector,
    count: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = count,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ProfileSectionCard(
    section: ProfileSection,
    onNotificationToggle: () -> Unit,
    onBiometricToggle: () -> Unit,
    onThemeToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = section.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            section.items.forEachIndexed { index, item ->
                ProfileMenuItem(
                    item = item,
                    onToggle = when (item.id) {
                        "notifications" -> onNotificationToggle
                        "biometric" -> onBiometricToggle
                        "theme" -> onThemeToggle
                        else -> {}
                    }
                )

                if (index < section.items.size - 1) {
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    item: ProfileMenuItem,
    onToggle: Any
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(item.iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            item.subtitle?.let { subtitle ->
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Switch or Arrow
        if (item.hasSwitch) {
            Switch(
                checked = item.switchState,
                onCheckedChange = { onToggle }
            )
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun LogoutSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = Color(0xFFF44336),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Logout",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF44336)
            )
        }
    }
}

@Composable
fun AppVersionSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bank App",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "Version 1.0.0",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}