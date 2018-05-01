package app.swapper.com.swapper.ui.activity

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import app.swapper.com.swapper.ui.factory.CreateNewItemViewModelFactory
import app.swapper.com.swapper.LocationData
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwaggerApp
import app.swapper.com.swapper.utils.Utils
import app.swapper.com.swapper.databinding.ActivityCreateNewItemBinding
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.ui.viewmodel.CreateNewItemViewModel
import kotlinx.android.synthetic.main.activity_create_new_item.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateNewItemActivity : BaseActivity() {

    private val REQUEST_CAPTURE_IMAGE = 100
    private var imageFilePath: String? = null
    private lateinit var photosArray : MutableList<File>

    private lateinit var viewModel: CreateNewItemViewModel
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val swaggerApp = application as SwaggerApp
        val apiService = swaggerApp.getRetrofit()
        user = swaggerApp.getUser()

        viewModel = ViewModelProviders.of(this, CreateNewItemViewModelFactory(apiService)).get(CreateNewItemViewModel::class.java)
        viewModel.isItemCreated.observe(this, android.arch.lifecycle.Observer { it?.let { if (it) finish() } })

        val binding: ActivityCreateNewItemBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_new_item)
        binding.createItemViewModel = viewModel

        photosArray = mutableListOf()

        takePhotoBtn.setOnClickListener {
            requestPermission(Manifest.permission.CAMERA)
        }

        send.setOnClickListener {
            Utils.ifNotNull(LocationData.location, user) { location, user ->
                run {
                    val item = Item(itemTitle.text.toString(),
                            description.text.toString(),
                            null,
                            location.latitude,
                            location.longitude,
                            user
                    )
                    viewModel.sendItemDataToServer(item)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )
        imageFilePath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAPTURE_IMAGE) {
                val file = File(imageFilePath)
                viewModel.addToCompressedImageArray(file)
            }
        }
    }

    override fun onPermissionGranted(grantedPermissions: Collection<String>) {
        if (Manifest.permission.CAMERA in grantedPermissions) {
            val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (pictureIntent.resolveActivity(packageManager) != null) {
                val photoFile = createImageFile()
                val photoURI = FileProvider.getUriForFile(this, application.packageName + ".provider", photoFile)
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
            }
        }
    }

    override fun onPermissionDenied(deniedPermissions: Collection<String>) {
        if (Manifest.permission.CAMERA in deniedPermissions) {

        }
    }
}
