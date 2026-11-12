package com.portfolio.subtrack.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.portfolio.subtrack.domain.model.Category
import com.portfolio.subtrack.domain.model.Subscription
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun SubscriptionItemCard(
    subscription: Subscription,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(subscription.colorHex)
    val daysLeft = calculateDaysUntilRenewal(subscription.nextRenewalDate)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(subscription.category),
                    contentDescription = subscription.category.label,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name & Renewal Countdown
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subscription.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        daysLeft < 0 -> "Overdue"
                        daysLeft == 0L -> "Renews today"
                        daysLeft == 1L -> "Renews tomorrow"
                        else -> "Renews in $daysLeft days"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (daysLeft <= 2) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Amount & Billing Cycle
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format(Locale.getDefault(), "%.2f", subscription.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subscription.billingCycle.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun getCategoryIcon(category: Category): ImageVector {
    return when (category) {
        Category.ENTERTAINMENT -> Icons.Rounded.Movie
        Category.UTILITY -> Icons.Rounded.Bolt
        Category.FITNESS -> Icons.Rounded.FitnessCenter
        Category.WORK -> Icons.Rounded.Work
        Category.OTHER -> Icons.Rounded.Subscriptions
    }
}

fun calculateDaysUntilRenewal(renewalMillis: Long): Long {
    val diff = renewalMillis - System.currentTimeMillis()
    return TimeUnit.MILLISECONDS.toDays(diff)
}
