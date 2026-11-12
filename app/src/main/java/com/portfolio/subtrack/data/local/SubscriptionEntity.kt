package com.portfolio.subtrack.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.portfolio.subtrack.domain.model.BillingCycle
import com.portfolio.subtrack.domain.model.Category
import com.portfolio.subtrack.domain.model.Subscription

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val billingCycle: String,
    val category: String,
    val nextRenewalDate: Long,
    val colorHex: Long
)

fun SubscriptionEntity.toDomain(): Subscription {
    return Subscription(
        id = id,
        name = name,
        amount = amount,
        billingCycle = BillingCycle.valueOf(billingCycle),
        category = Category.valueOf(category),
        nextRenewalDate = nextRenewalDate,
        colorHex = colorHex
    )
}

fun Subscription.toEntity(): SubscriptionEntity {
    return SubscriptionEntity(
        id = id,
        name = name,
        amount = amount,
        billingCycle = billingCycle.name,
        category = category.name,
        nextRenewalDate = nextRenewalDate,
        colorHex = colorHex
    )
}
