package com.portfolio.subtrack.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.subtrack.data.local.ThemePreferences
import com.portfolio.subtrack.domain.model.Subscription
import com.portfolio.subtrack.domain.usecase.SubscriptionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: SubscriptionUseCases,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var recentlyDeletedSubscription: Subscription? = null

    init {
        observeData()
    }

    private fun observeData() {
        combine(
            useCases.getSubscriptions(),
            useCases.getMonthlyTotal(),
            themePreferences.isDarkMode
        ) { subscriptions, monthlyTotal, isDark ->
            _uiState.update { currentState ->
                currentState.copy(
                    subscriptions = subscriptions,
                    totalMonthlySpend = monthlyTotal,
                    isDarkMode = isDark,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SaveSubscription -> {
                viewModelScope.launch {
                    useCases.addSubscription(event.subscription)
                    _uiState.update { it.copy(isAddSheetOpen = false, editingSubscription = null) }
                }
            }
            is HomeEvent.DeleteSubscription -> {
                viewModelScope.launch {
                    recentlyDeletedSubscription = event.subscription
                    useCases.deleteSubscription(event.subscription)
                }
            }
            is HomeEvent.RestoreDeletedSubscription -> {
                viewModelScope.launch {
                    recentlyDeletedSubscription?.let { sub ->
                        useCases.addSubscription(sub)
                        recentlyDeletedSubscription = null
                    }
                }
            }
            is HomeEvent.OpenAddSheet -> {
                _uiState.update {
                    it.copy(
                        isAddSheetOpen = true,
                        editingSubscription = event.subscriptionToEdit
                    )
                }
            }
            is HomeEvent.CloseAddSheet -> {
                _uiState.update {
                    it.copy(isAddSheetOpen = false, editingSubscription = null)
                }
            }
            is HomeEvent.ToggleTheme -> {
                viewModelScope.launch {
                    themePreferences.setDarkMode(!_uiState.value.isDarkMode)
                }
            }
        }
    }
}
