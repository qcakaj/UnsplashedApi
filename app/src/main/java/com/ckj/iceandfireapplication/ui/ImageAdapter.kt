package com.ckj.iceandfireapplication.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ckj.iceandfireapplication.R
import com.ckj.iceandfireapplication.databinding.ItemImageLayoutBinding
import com.ckj.iceandfireapplication.db.entities.ImageCacheEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ImageAdapter(private val clickListener: ImageClickListener) : PagingDataAdapter<ImageCacheEntity,
        RecyclerView.ViewHolder>(ItemImageDiffCallback()) {

        private val adapterScope = CoroutineScope(Dispatchers.Default)


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is ViewHolder -> {
                    val imageItem = getItem(position)
                    if (imageItem != null) {
                        holder.bind(clickListener, imageItem)
                    }


                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder.from(parent)
        }


        class ViewHolder private constructor(val binding: ItemImageLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(
                clickListener: ImageClickListener,
                item: ImageCacheEntity,

                ) {
                with(binding) {

                    binding.imagePlaceholder.load(item.downloadUrl) {
                        crossfade(true)
                    }

                    root.setOnClickListener {
                        clickListener.onClick(item)
                    }
                    root.setOnLongClickListener {
                        clickListener.onLongClick(item)
                        return@setOnLongClickListener true
                    }
                }
            }

            companion object {
                fun from(parent: ViewGroup): ViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = ItemImageLayoutBinding.inflate(layoutInflater, parent, false)

                    return ViewHolder(binding)
                }
            }
        }
    }

    /**
     * Callback for calculating the diff between two non-null items in a list.
     *
     * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
     * list that's been passed to `submitList`.
     */
    class ItemImageDiffCallback : DiffUtil.ItemCallback<ImageCacheEntity>() {
        override fun areItemsTheSame(oldItem: ImageCacheEntity, newItem: ImageCacheEntity): Boolean {
            return oldItem.downloadUrl == newItem.downloadUrl
        }

        override fun areContentsTheSame(oldItem: ImageCacheEntity, newItem: ImageCacheEntity): Boolean {
            return oldItem == newItem
        }
    }

    class ImageClickListener(
        val clickListener: (item: ImageCacheEntity) -> Unit,
        val longClickListener: (url: ImageCacheEntity) -> Unit
    ) {
        fun onClick(image: ImageCacheEntity) = clickListener(image)
        fun onLongClick(image: ImageCacheEntity) = longClickListener(image)
    }