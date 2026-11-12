package com.portfolio.subtrack.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.portfolio.subtrack.presentation.components.AddSubscriptionSheet
import com.portfolio.subtrack.presentation.components.SubscriptionItemCard
import com.portfolio.subtrack.presentation.components.SummaryCard
import com.portfolio.subtrack.presentation.theme.PrimaryAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SubTrack",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onEvent(HomeEvent.ToggleTheme) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = if (state.isDarkMode) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onEvent(HomeEvent.OpenAddSheet()) },
                containerColor = PrimaryAccent,
                modifier = Modifier.defaultMinSize(minWidth = 40.dp, minHeight = 40.dp),
                contentColor = Color.White,
                shape = RoundedCornerShape(4.dp),
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Add New", fontWeight = FontWeight.SemiBold) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            SummaryCard(
                monthlyTotal = state.totalMonthlySpend,
                activeCount = state.subscriptions.size
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your Subscriptions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (state.subscriptions.isEmpty() && !state.isLoading) {
                EmptyStateView()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 84.dp)
                ) {
                    items(
                        items = state.subscriptions,
                        key = { it.id }
                    ) { subscription ->
                        val dismissState = rememberSwipeToDismissBoxState()

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            onDismiss = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.onEvent(HomeEvent.DeleteSubscription(subscription))
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "${subscription.name} deleted",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.onEvent(HomeEvent.RestoreDeletedSubscription)
                                        }
                                    }
                                }
                            },
                            backgroundContent = {
                                val isSwiping = dismissState.dismissDirection != SwipeToDismissBoxValue.Settled
                                if (isSwiping) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(MaterialTheme.colorScheme.errorContainer)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }

                        ) {
                            SubscriptionItemCard(
                                subscription = subscription,
                                onClick = { viewModel.onEvent(HomeEvent.OpenAddSheet(subscription)) }
                            )
                        }
                    }
                }
            }
        }

        if (state.isAddSheetOpen) {
            AddSubscriptionSheet(
                existingSubscription = state.editingSubscription,
                onDismiss = { viewModel.onEvent(HomeEvent.CloseAddSheet) },
                onSave = { viewModel.onEvent(HomeEvent.SaveSubscription(it)) }
            )
        }
    }
}

@Composable
private fun EmptyStateView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No subscriptions yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Tap + to track your recurring expenses",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
