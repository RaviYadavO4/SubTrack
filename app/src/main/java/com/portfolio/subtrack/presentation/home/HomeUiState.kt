package com.portfolio.subtrack.presentation.home

import com.portfolio.subtrack.domain.model.Subscription

data class HomeUiState(
    val subscriptions: List<Subscription> = emptyList(),
    val totalMonthlySpend: Double = 0.0,
    val isLoading: Boolean = true,
    val isAddSheetOpen: Boolean = false,
    val editingSubscription: Subscription? = null,
    val isDarkMode: Boolean = true
)

sealed interface HomeEvent {
    data class SaveSubscription(val subscription: Subscription) : HomeEvent
    data class DeleteSubscription(val subscription: Subscription) : HomeEvent
    data object RestoreDeletedSubscription : HomeEvent
    data class OpenAddSheet(val subscriptionToEdit: Subscription? = null) : HomeEvent
    data object CloseAddSheet : HomeEvent
    data object ToggleTheme : HomeEvent
}
