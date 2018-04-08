package app.swapper.com.swapper.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwaggerApp
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.model.CreationPresenterImpl
import app.swapper.com.swapper.presenter.CreationPresenter
import app.swapper.com.swapper.storage.SharedPreferencesManager
import app.swapper.com.swapper.view.ItemCreationView
import kotlinx.android.synthetic.main.activity_create_new_item.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateNewItemActivity : BaseActivity(), ItemCreationView {

    private val REQUEST_CAPTURE_IMAGE = 100
    private var imageFilePath: String? = null
    private lateinit var photosArray : MutableList<File>
    private lateinit var creationPresenter : CreationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_item)

        photosArray = mutableListOf()
        creationPresenter = CreationPresenterImpl(this)

        takePhotoBtn.setOnClickListener { cameraTask() }

        send.setOnClickListener {
            val application = applicationContext as SwaggerApp
            val user = application.getUser()
            user?.let {
                location?.let {
                    val item = Item(itemTitle.text.toString(),
                            description.text.toString(),
                            null,
                            it.latitude,
                            it.longitude,
                            user
                    )
                    creationPresenter.sendItemDataToServer(item, photosArray)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    @AfterPermissionGranted(2)
    private fun cameraTask() {
        if (hasCameraPermission()) {
            openCameraIntent()
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    "Because we need camera",
                    2,
                    Manifest.permission.CAMERA)
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

    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(packageManager) != null){
            val photoFile = createImageFile()
            val photoURI = FileProvider.getUriForFile(this, application.packageName + ".provider", photoFile)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
        }
    }

    override fun itemCreated() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAPTURE_IMAGE) {
                val file = File(imageFilePath)
                photosArray.add(file)
            }
        }
    }
}
