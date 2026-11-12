package com.portfolio.subtrack.data.repository

import com.portfolio.subtrack.data.local.SubscriptionDao
import com.portfolio.subtrack.data.local.toDomain
import com.portfolio.subtrack.data.local.toEntity
import com.portfolio.subtrack.domain.model.Subscription
import com.portfolio.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val dao: SubscriptionDao
) : SubscriptionRepository {

    override fun getAllSubscriptions(): Flow<List<Subscription>> {
        return dao.getAllSubscriptions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSubscription(subscription: Subscription) {
        dao.insertSubscription(subscription.toEntity())
    }

    override suspend fun deleteSubscription(subscription: Subscription) {
        dao.deleteSubscription(subscription.toEntity())
    }
}
