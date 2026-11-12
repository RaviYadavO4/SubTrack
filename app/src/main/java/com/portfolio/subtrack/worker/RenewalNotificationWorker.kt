package com.portfolio.subtrack.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.portfolio.subtrack.R
import com.portfolio.subtrack.domain.repository.SubscriptionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class RenewalNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SubscriptionRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val subscriptions = repository.getAllSubscriptions().first()
        val now = System.currentTimeMillis()
        val oneDayMillis = TimeUnit.DAYS.toMillis(1)

        val upcoming = subscriptions.filter { sub ->
            val diff = sub.nextRenewalDate - now
            diff in 0..oneDayMillis
        }

        if (upcoming.isNotEmpty()) {
            createNotificationChannel()
            upcoming.forEach { sub ->
                sendNotification(
                    id = sub.id,
                    title = "Upcoming Renewal: ${sub.name}",
                    message = "Your ${sub.name} subscription of ₹${sub.amount} renews within 24 hours."
                )
            }
        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Subscription Renewals",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts before your subscription renews"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(id: Int, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }

    companion object {
        const val CHANNEL_ID = "subtrack_renewal_channel"
    }
}
