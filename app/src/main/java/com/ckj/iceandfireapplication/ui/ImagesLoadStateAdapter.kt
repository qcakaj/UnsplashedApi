package com.ckj.iceandfireapplication.ui

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class ImagesLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<ImagesLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: ImagesLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ImagesLoadStateViewHolder {
        return ImagesLoadStateViewHolder.create(parent, retry)
    }
}