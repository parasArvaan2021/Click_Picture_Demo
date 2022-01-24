package com.app.clickpicturedemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.clickpicturedemo.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var uri: Uri

    private lateinit var imageAdapter: ImageAdapter

    private val resultLauncher =
        registerForActivityResult(object : ActivityResultContracts.TakePicture() {
            @SuppressLint("MissingSuperCall")
            override fun createIntent(context: Context, input: Uri): Intent {
                val intent = super.createIntent(context, input)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    intent.clipData = ClipData.newRawUri("", input)
                    intent.addFlags(
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                return intent
            }
        }) { success ->
            if (success) {
                loadImageList()
            } else {
                contentResolver.delete(uri, null, null)
            }
        }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isAllGranted = true
            permissions.entries.forEach {
                if (!it.value) {
                    isAllGranted = false
                }
            }
            if (isAllGranted) {
                launchCameraIntent()
            } else {
                Log.i("DEBUG", "permission denied")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageAdapter = ImageAdapter(this@MainActivity, arrayListOf())
        binding.rvImageList.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = imageAdapter
        }
        loadImageList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_camera -> {
                requestPermission.launch(
                    arrayOf(
                        Manifest.permission.CAMERA
                    )
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun launchCameraIntent() {
        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            photoFile
        )

        resultLauncher.launch(uri)
    }

    private fun loadImageList() {
        val gpath: String = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
        val fullPath = File(gpath + File.separator)
        Log.e("fullPath", "" + gpath)
        imageReader(fullPath)
    }

    private fun imageReader(root: File) {
        val fileList: ArrayList<File> = ArrayList()
        val listAllFiles = root.listFiles()
        val imageList: ArrayList<ImageModel> = arrayListOf()

        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                fileList.add(currentFile.absoluteFile)
                imageList.add(
                    ImageModel(
                        currentFile.name,
                        FileProvider.getUriForFile(
                            this@MainActivity,
                            applicationContext.packageName + ".provider",
                            currentFile
                        )
                    )
                )
            }
            Log.e(TAG, "imageReaderNew: ${imageList.size}")
            imageAdapter.addNewItem(imageList)
        }

    }

}