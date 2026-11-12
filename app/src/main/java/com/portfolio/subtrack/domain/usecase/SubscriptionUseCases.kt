package com.portfolio.subtrack.domain.usecase

import com.portfolio.subtrack.domain.model.BillingCycle
import com.portfolio.subtrack.domain.model.Subscription
import com.portfolio.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class SubscriptionUseCases(
    val getSubscriptions: GetSubscriptionsUseCase,
    val addSubscription: AddSubscriptionUseCase,
    val deleteSubscription: DeleteSubscriptionUseCase,
    val getMonthlyTotal: GetMonthlyTotalUseCase
)

class GetSubscriptionsUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<List<Subscription>> = repository.getAllSubscriptions()
}

class AddSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(subscription: Subscription) {
        if (subscription.name.isBlank() || subscription.amount <= 0) {
            throw IllegalArgumentException("Please enter a valid name and amount")
        }
        repository.insertSubscription(subscription)
    }
}

class DeleteSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(subscription: Subscription) {
        repository.deleteSubscription(subscription)
    }
}

class GetMonthlyTotalUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<Double> {
        return repository.getAllSubscriptions().map { list ->
            list.sumOf { sub ->
                if (sub.billingCycle == BillingCycle.MONTHLY) sub.amount
                else sub.amount / 12.0
            }
        }
    }
}
