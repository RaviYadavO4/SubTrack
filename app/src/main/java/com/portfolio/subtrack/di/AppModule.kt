package com.portfolio.subtrack.di

import android.app.Application
import androidx.room.Room
import com.portfolio.subtrack.data.local.SubTrackDatabase
import com.portfolio.subtrack.data.repository.SubscriptionRepositoryImpl
import com.portfolio.subtrack.domain.repository.SubscriptionRepository
import com.portfolio.subtrack.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): SubTrackDatabase {
        return Room.databaseBuilder(
            app,
            SubTrackDatabase::class.java,
            "subtrack_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(db: SubTrackDatabase): SubscriptionRepository {
        return SubscriptionRepositoryImpl(db.subscriptionDao)
    }

    @Provides
    @Singleton
    fun provideUseCases(repository: SubscriptionRepository): SubscriptionUseCases {
        return SubscriptionUseCases(
            getSubscriptions = GetSubscriptionsUseCase(repository),
            addSubscription = AddSubscriptionUseCase(repository),
            deleteSubscription = DeleteSubscriptionUseCase(repository),
            getMonthlyTotal = GetMonthlyTotalUseCase(repository)
        )
    }
}
