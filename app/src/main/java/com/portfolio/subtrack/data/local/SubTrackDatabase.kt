package com.portfolio.subtrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SubscriptionEntity::class], version = 1, exportSchema = false)
abstract class SubTrackDatabase : RoomDatabase() {
    abstract val subscriptionDao: SubscriptionDao
}
