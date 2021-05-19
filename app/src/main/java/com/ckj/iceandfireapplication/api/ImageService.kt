package com.ckj.iceandfireapplication.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageService {
    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
//    https://picsum.photos/v2/list?page=2&limit=100

    @GET("list")
    suspend fun getImageList(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<ImageNetworkEntity>>

}