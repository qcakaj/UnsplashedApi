package com.ckj.iceandfireapplication.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ckj.iceandfireapplication.data.ImageRepository
import com.ckj.iceandfireapplication.db.ApplicationDatabase
import com.ckj.iceandfireapplication.db.entities.ImageCacheEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imageRepository: ImageRepository,
    private val applicationDatabase: ApplicationDatabase

) : ViewModel() {


    private var _imageUrl: String = ""
    val imageUrl: String
        get() = _imageUrl


    private val _isGranted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isGranted: StateFlow<Boolean>
        get() = _isGranted

    init {
        setStateEvent(MainStateEvent.GetImageEvent)
        testGrouping()
    }

    private var imageResult: Flow<PagingData<ImageCacheEntity>>? = null

    @ExperimentalPagingApi
    fun getImages(): Flow<PagingData<ImageCacheEntity>> {
        val lastResult = imageResult
        if ( lastResult != null) {
            return lastResult
        }
        val newResult: Flow<PagingData<ImageCacheEntity>> = imageRepository.getImages()
        imageResult = newResult
        return newResult
    }

    fun testGrouping() {
       val randomList = mutableListOf<ImageCacheEntity>(
           ImageCacheEntity("1","Qendrim",url="",downloadUrl = "",width = null,height = null),
           ImageCacheEntity("1","Amir",url="",downloadUrl = "",width = null,height = null),
           ImageCacheEntity("1","Armir",url="",downloadUrl = "",width = null,height = null),
           ImageCacheEntity("1","Besart",url="",downloadUrl = "",width = null,height = null),
           ImageCacheEntity("1","Briona",url="",downloadUrl = "",width = null,height = null),
           ImageCacheEntity("1","derick",url="",downloadUrl = "",width = null,height = null),
           )
    val group=   randomList.groupBy { it.author.first() }
        group.forEach { (s, list) ->

            val items = listOf(s.toString()) +  list

            Log.e("items",items.toString())


        }
    }

        @ExperimentalPagingApi
        fun setStateEvent(mainStateEvent: MainStateEvent) {
        viewModelScope.launch {
            when (mainStateEvent) {
                MainStateEvent.GetImageEvent -> {
//                    getImages()
                }
                MainStateEvent.None -> {
                    _isGranted.value= false

                }

                MainStateEvent.DownloadImageEvent -> {
                    _isGranted.value = true
                }
            }
        }
    }

    fun setDownloadUrl(downloadUrl: String) {
        _imageUrl = ""
        _imageUrl = downloadUrl
    }

}

sealed class MainStateEvent {
    object GetImageEvent : MainStateEvent()
    object DownloadImageEvent : MainStateEvent()
    object None : MainStateEvent()
}