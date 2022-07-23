package com.falcon.findingfalcon.di

import com.falcon.findingfalcon.data.FalconRepositoryImpl
import com.falcon.findingfalcon.data.network.WebService
import com.falcon.findingfalcon.domain.FalconRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideFalconRepository(webService: WebService): FalconRepository {
        return FalconRepositoryImpl(webService)
    }
}