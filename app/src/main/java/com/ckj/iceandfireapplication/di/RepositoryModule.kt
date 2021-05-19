package com.ckj.iceandfireapplication.di

import com.ckj.iceandfireapplication.data.ImageRepository
import com.ckj.iceandfireapplication.api.ImageService
import com.ckj.iceandfireapplication.api.NetworkMapper
import com.ckj.iceandfireapplication.db.ApplicationDatabase
import com.ckj.iceandfireapplication.db.CacheMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideImageRepository(
        applicationDatabase: ApplicationDatabase,
        imageService: ImageService,
        cacheMapper: CacheMapper,
        networkMapper: NetworkMapper
    ) : ImageRepository {
        return ImageRepository(
            applicationDatabase = applicationDatabase,
            imageService = imageService,
            cacheMapper = cacheMapper,
            networkMapper = networkMapper
        )
    }
}