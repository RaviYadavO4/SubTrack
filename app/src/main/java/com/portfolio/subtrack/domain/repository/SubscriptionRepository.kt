package com.portfolio.subtrack.domain.repository

import com.portfolio.subtrack.domain.model.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getAllSubscriptions(): Flow<List<Subscription>>
    suspend fun insertSubscription(subscription: Subscription)
    suspend fun deleteSubscription(subscription: Subscription)
}
