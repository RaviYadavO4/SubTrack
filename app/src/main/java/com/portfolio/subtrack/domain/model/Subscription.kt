package com.portfolio.subtrack.domain.model

data class Subscription(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val billingCycle: BillingCycle,
    val category: Category,
    val nextRenewalDate: Long,
    val colorHex: Long
)

enum class BillingCycle(val label: String) {
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

enum class Category(val label: String) {
    ENTERTAINMENT("Entertainment"),
    UTILITY("Utility"),
    FITNESS("Fitness"),
    WORK("Work"),
    OTHER("Other")
}
