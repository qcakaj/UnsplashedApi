package com.ckj.iceandfireapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.ckj.iceandfireapplication.R
import com.ckj.iceandfireapplication.databinding.ItemImageLoadStateBinding

class ImagesLoadStateViewHolder(
        private val binding: ItemImageLoadStateBinding,
        retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ImagesLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_load_state, parent, false)
            val binding = ItemImageLoadStateBinding.bind(view)
            return ImagesLoadStateViewHolder(binding, retry)
        }
    }
}