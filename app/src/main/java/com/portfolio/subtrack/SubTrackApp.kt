package com.portfolio.subtrack

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.portfolio.subtrack.worker.RenewalNotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class SubTrackApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleDailyRenewalCheck()
    }

    private fun scheduleDailyRenewalCheck() {
        val workRequest = PeriodicWorkRequestBuilder<RenewalNotificationWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_renewal_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
