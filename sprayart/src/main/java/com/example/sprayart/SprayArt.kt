package com.example.sprayart

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import cn.easyar.Engine
import android.provider.MediaStore
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import android.view.View
import com.example.data.network.api.APICommunication
import com.example.data.network.models.ApiResponse
import com.example.data.network.models.request.TagRequestApi
import com.example.data.network.models.response.Tag
import com.example.data.portability.Consumer
import com.example.data.util.UploadUtil
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_spray_art.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class SprayArt: AppCompatActivity() {
    companion object {
        private val key = "wcC0IXh6b9hMnX4Ol9tj7eLoZQpTRyodekJBJ990aSpqmlnCwU9ufzqYxzWgnTswM3b6UTpo3oasgjdsMiM33adGg4M0p2AQWbM3LT70MnQwNlLbRLr1nA8e66dBT6t0Dsd8VQFXCDLNfdGZf1JwmHiMoi7S4wivaXAcuoS0KCmD3iMKcAwOo4COR9PDGXLrEUjByuUx\n"
    }

    private var glView: GLView? = null
    private val REQUEST_IMAGE_CAPTURE = 100
    private val REQUEST_SELECT_PICTURE = 101
    private var mCurrentPhotoPath = ""
    private val helper by lazy { TextureHelper(BitmapFactory.decodeResource(resources, R.drawable.sausages)) }
    private val photosMap = mutableMapOf<String, String>()
    private var selectedGrafitti : Uri = Uri.EMPTY
    private var api: APICommunication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spray_art)
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.")
        }

        glView = GLView(this, photosMap)
        preview.addView(glView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        addWallBtn.setOnClickListener {
            dispatchTakePictureIntent()
        }
        addGrafityBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_PICTURE)
        }
        plusBtn.setOnClickListener{

        }
        minusBtn.setOnClickListener {

        }
        doneBtn.setOnClickListener {
            plusBtn.visibility = View.GONE
            minusBtn.visibility = View.GONE
            doneBtn.visibility = View.GONE
            imageView.visibility = View.GONE
            photosMap[selectedGrafitti.toString()] = mCurrentPhotoPath
            reloadGlView()
            //save data to server here
            val file1 = generateFileFromURI(selectedGrafitti)
            val file2 = File(mCurrentPhotoPath)
            UploadUtil.uploadToFirebase(file1, this::onImageUploaded, this::onImageUploadError)
            UploadUtil.uploadToFirebase(file2, this::onImageUploaded, this::onImageUploadError)
        }

        api = APICommunication.getInstance(this)
    }

    private fun generateFileFromURI(uri: Uri): File {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val file = createImageFile(false)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (out != null) {
                    out.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return file
    }

    override fun onPause() {
        super.onPause()
        preview.removeView(glView)
    }

    override fun onResume() {
        super.onResume()
        if (glView!!.parent == null)
            preview.addView(glView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == FragmentActivity.RESULT_OK)
        {
            imageView.visibility = View.VISIBLE
            imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath))

        } else if(requestCode == REQUEST_SELECT_PICTURE && resultCode == RESULT_OK) {
            plusBtn.visibility = View.VISIBLE
            minusBtn.visibility = View.VISIBLE
            doneBtn.visibility = View.VISIBLE
            selectedGrafitti = data!!.data


        }
    }

    private fun reloadGlView()
    {
        preview.removeView(glView)
        glView = GLView(this, photosMap)
        preview.addView(glView)
    }

    private fun createImageFile(setPhotoPath : Boolean): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        if(setPhotoPath)
            mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile : File = createImageFile(true)
            val photoURI = FileProvider.getUriForFile(this@SprayArt, "com.koshka.origami.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun onImageUploaded(uri: Uri?) {
//        val tagObservable = api.postTags(TagRequestApi())
//        APICommunication.execute(tagObservable, Consumer<ApiResponse<List<Tag>>> { this.onTagsReceived(it) }, Consumer<Throwable> { this.onTagsReceiveError(it) })
    }

    fun onImageUploadError(exeception: Exception) {

    }
}