package com.ckj.iceandfireapplication.data

import androidx.paging.*
import com.ckj.iceandfireapplication.data.ImageRemoteMediator
import com.ckj.iceandfireapplication.api.ImageService
import com.ckj.iceandfireapplication.api.ImageService.Companion.NETWORK_PAGE_SIZE
import com.ckj.iceandfireapplication.api.NetworkMapper
import com.ckj.iceandfireapplication.db.ApplicationDatabase
import com.ckj.iceandfireapplication.db.CacheMapper
import com.ckj.iceandfireapplication.db.entities.ImageCacheEntity
import kotlinx.coroutines.flow.Flow

class ImageRepository constructor(
    private val applicationDatabase: ApplicationDatabase,
    private val imageService: ImageService,
    private val cacheMapper: CacheMapper,
    private val networkMapper: NetworkMapper
) {
    private val pagingSourceFactory =  {
        applicationDatabase.imageDao().getImages()
    }


    @ExperimentalPagingApi
    fun getImages(): Flow<PagingData<ImageCacheEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory=pagingSourceFactory,
            remoteMediator = ImageRemoteMediator(networkMapper = networkMapper, cacheMapper = cacheMapper, service = imageService,applicationDatabase = applicationDatabase)
        ).flow
    }

}