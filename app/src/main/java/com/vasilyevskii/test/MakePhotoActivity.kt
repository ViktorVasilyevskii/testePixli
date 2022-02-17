package com.vasilyevskii.test

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.camera.core.*

import androidx.camera.view.PreviewView
import java.io.File


class MakePhotoActivity : AppCompatActivity(){

    private val PERMISSION_REQUEST_CAMERA = 83854
    private val nameFileImg = "filename.jpg"

    private var imageCapture: ImageCapture? = null


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_photo)

        val targetEditText = findViewById<EditText>(R.id.edit_text_target)
        targetEditText.setText(intent.getStringExtra(App().namePutExtraTarget))

        buttonTakePhotoListener()
        buttonTakePhotoCameraX()
    }


    private fun buttonTakePhotoListener(){
        findViewById<Button>(R.id.button_take_photo).setOnClickListener {
                checkPermission()
        }
    }

    private fun buttonTakePhotoCameraX(){
        findViewById<Button>(R.id.button_take_photo_camerax).setOnClickListener {
            takePhoto()
        }
    }

    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                showDialogOK(getString(R.string.text_request_permission_camera)) { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            requestPermissions()
                            startCamera()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> finish()
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
            }
        }else startCamera()
    }

    private fun requestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CAMERA)
            return false
        }
        return true
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(this.getString(android.R.string.ok), okListener)
            .setNegativeButton(this.getString(android.R.string.cancel), okListener)
            .create()
            .show()
    }


    private fun startCamera(){

        findViewById<LinearLayout>(R.id.liner_layout_info_target).visibility = View.GONE
        findViewById<LinearLayout>(R.id.liner_layout_camera).visibility = View.VISIBLE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { mPreview ->
                    mPreview.setSurfaceProvider(
                        findViewById<PreviewView>(R.id.preview_camera).surfaceProvider
                    )
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector,
                    preview, imageCapture
                )
            }catch (ex: Exception){
                Log.d("startCamera", "startCamera Fail:", ex)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        val imageCapture = imageCapture ?: return
        val photoFile = File(filesDir, nameFileImg)


        val outputOption = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object :ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("takePhoto", "save: $savedUri")
                    alertDialogRequestSendPhoto()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("takePhoto", "onError: ${exception.message}", exception)
                }

            }
        )

    }


    private fun alertDialogRequestSendPhoto(){
        val alertRequestSendPhoto = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.alert_send_photo, null)

        alertRequestSendPhoto.setView(view)
        alertRequestSendPhoto.setCancelable(false)

        view.findViewById<Button>(R.id.button_alert_reshoot).setOnClickListener {
            onRestart()
            startCamera()
        }

        view.findViewById<Button>(R.id.button_alert_no).setOnClickListener {
            onRestart()
        }


        val dialog = alertRequestSendPhoto.create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.show()


    }

    override fun onRestart() {
        super.onRestart()
        val intent = intent
        finish()
        startActivity(intent)
    }


}