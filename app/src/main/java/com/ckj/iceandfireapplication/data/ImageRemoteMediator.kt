package com.ckj.iceandfireapplication.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ckj.iceandfireapplication.api.ImageService
import com.ckj.iceandfireapplication.api.NetworkMapper
import com.ckj.iceandfireapplication.db.*
import com.ckj.iceandfireapplication.db.entities.ImageCacheEntity
import com.ckj.iceandfireapplication.db.entities.RemoteKeysEntity
import okio.IOException
import retrofit2.HttpException

private const val API_STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class ImageRemoteMediator(
    private val networkMapper: NetworkMapper,
    private val cacheMapper: CacheMapper,
    private val service: ImageService,
    private val applicationDatabase: ApplicationDatabase
) : RemoteMediator<Int, ImageCacheEntity>() {


    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
    override suspend fun load(loadType: LoadType, state: PagingState<Int, ImageCacheEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: API_STARTING_PAGE_INDEX            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeysEntity becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey

            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeysEntity becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val apiResponse = service.getImageList(page =  page, state.config.pageSize)

            val images = apiResponse.body()?.let { networkMapper.mapFromEntityList(it) }
            val endOfPaginationReached = images?.isEmpty() == true
            applicationDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    applicationDatabase.remoteKeysDao().clearRemoteKeys()
                    applicationDatabase.imageDao().clearImages()
                }
                val prevKey = if (page == API_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = images?.map {
                    RemoteKeysEntity(imageId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                if (keys != null) {
                    applicationDatabase.remoteKeysDao().insertAll(keys)
                }

                images?.let {
                    cacheMapper.mapToEntityList(
                        it
                    )
                }?.let { applicationDatabase.imageDao().insertAll(it) }

            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
   }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ImageCacheEntity>
    ): RemoteKeysEntity? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                applicationDatabase.remoteKeysDao().remoteKeysRepoId(repoId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ImageCacheEntity>): RemoteKeysEntity? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first items retrieved
                applicationDatabase.remoteKeysDao().remoteKeysRepoId(repo.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ImageCacheEntity>): RemoteKeysEntity? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { image ->
                // Get the remote keys of the last item retrieved
                applicationDatabase.remoteKeysDao().remoteKeysRepoId(image.id)
            }
    }
}