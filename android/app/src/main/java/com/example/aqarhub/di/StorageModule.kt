package com.example.aqarhub.di

import android.content.Context
import com.example.aqarhub.data.local.EncryptedSharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferencesManager(
        @ApplicationContext context: Context
    ): EncryptedSharedPreferencesManager {
        return EncryptedSharedPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthPreferences(
        @ApplicationContext context: Context
    ): com.example.aqarhub.data.local.AuthPreferences {
        return com.example.aqarhub.data.local.AndroidAuthPreferencesFactory.create(context)
    }
}
