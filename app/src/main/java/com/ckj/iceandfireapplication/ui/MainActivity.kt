package com.ckj.iceandfireapplication.ui

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.*
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.ckj.iceandfireapplication.R
import com.ckj.iceandfireapplication.databinding.ActivityMainBinding
import com.ckj.iceandfireapplication.databinding.ItemImageLayoutBinding
import com.ckj.iceandfireapplication.db.entities.ImageCacheEntity
import com.ckj.iceandfireapplication.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


@ExperimentalPagingApi
@InternalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) hideSystemUI() else showSystemUI()
//    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: ImageAdapter

    //Permission Request Handler
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var imagesJob: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupRecyclerView()
        getImages()
//        initImages()
        setPermissionCallback()
        setupView()
        setupObservers()
    }

    //Allowing activity to automatically handle permission request
    private fun setPermissionCallback() {
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    viewModel.setStateEvent(MainStateEvent.DownloadImageEvent)
//                    getBitmapFromUrl(edit_text_image_url.text.toString().trim())
                }
            }
    }

    //function to check and request storage permission
    private fun checkPermissionAndDownloadBitmap(bitmapURL: String) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                getBitmapFromUrl(bitmapURL)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                showPermissionRequestDialog(
                    getString(R.string.permission_title),
                    getString(R.string.write_permission_request)
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    fun String.printPhoto() =
        lifecycleScope.launch(Dispatchers.IO) {
            val request = ImageRequest.Builder(this@MainActivity)
                .data(this@printPhoto)
                .build()
            try {
                val bitmap =
                    (this@MainActivity.imageLoader.execute(request).drawable as BitmapDrawable).bitmap
                withContext(Dispatchers.Main) {
                    doPhotoPrint { printHelper ->
                        printHelper.printBitmap("test print", bitmap)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    toast(e.message)

                }
            }
        }


    //this function will fetch the Bitmap from the given URL
    private fun getBitmapFromUrl(bitmapURL: String) = lifecycleScope.launch(Dispatchers.IO) {
        val request = ImageRequest.Builder(this@MainActivity)
            .data(bitmapURL)
            .build()
        try {
            val downloadedBitmap =
                (this@MainActivity.imageLoader.execute(request).drawable as BitmapDrawable).bitmap
            saveMediaToStorage(downloadedBitmap)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                toast(e.message)
            }
        }
    }

    //the function I already explained, it is used to save the Bitmap to external storage
    @ExperimentalPagingApi
    private fun saveMediaToStorage(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.IO) {
            val filename = "${System.currentTimeMillis()}.jpg"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                viewModel.setStateEvent(MainStateEvent.None)
                withContext(Dispatchers.Main) {
                    toast("Saved to Photos")
                }
            }
        }
    }

    private fun setupView() {
        setupBottomBar()
    }

    private fun setupBottomBar() {
        binding.bottomAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Navigate", Toast.LENGTH_SHORT).show()
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.more -> {
                    Toast.makeText(this, "More", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    @ExperimentalPagingApi
    private fun setupRecyclerView() {

        val staggeredGridLayoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager
        adapter = ImageAdapter(ImageClickListener({ image ->
            viewModel.setDownloadUrl(image.downloadUrl)
            checkPermissionAndDownloadBitmap(image.downloadUrl)
        }, { url ->
            url.downloadUrl.printPhoto()
        }))


        binding.recyclerView.adapter =
            adapter.withLoadStateHeaderAndFooter(header = ImagesLoadStateAdapter { adapter.retry() },
                footer = ImagesLoadStateAdapter { adapter.retry() })

        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Only show the list if refresh succeeds.
            binding.recyclerView.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }
    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
    @ExperimentalPagingApi
    private fun setupObservers() {
        binding.retryButton.setOnClickListener { adapter.retry() }

        viewModel.isGranted.asLiveData().observe(this) { isGranted ->
            if (isGranted) getBitmapFromUrl(viewModel.imageUrl)
        }

    }

    @ExperimentalPagingApi
    private fun getImages() {
        // Make sure we cancel the previous job before creating a new one
        imagesJob?.cancel()
        imagesJob = lifecycleScope.launch {
            viewModel.getImages().collectLatest {
                    adapter.submitData(it)
            }
        }
    }

    private fun initImages() {

// Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect {
                    binding.recyclerView.scrollToPosition(0)
                }


        }
    }

}

