package com.qi.david.wallpaperchanger.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.qi.david.wallpaperchanger.R
import com.qi.david.wallpaperchanger.utils.styleLightStatusBarColor
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.DocumentsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.qi.david.wallpaperchanger.model.DATE
import com.qi.david.wallpaperchanger.utils.ASFUriHelper
import com.qi.david.wallpaperchanger.model.Setting
import com.qi.david.wallpaperchanger.utils.SettingStorageHelper
import com.qi.david.wallpaperchanger.utils.WallpaperChangerScheduler


private const val PICK_DIRECTORY_REQUEST_CODE = 160
private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 999

class MainActivity : AppCompatActivity(), MainActivityView {

    private val presenter = MainActivityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        styleLightStatusBarColor(R.color.gainsboro)
        setupListeners()
        presenter.bindStorage(SettingStorageHelper(this))
    }

    private fun setupListeners() {

        mainWallpaperFolderButton.setOnClickListener {
            presenter.onSelectFolderButtonPressed()
        }

        mainSaveButton.setOnClickListener {
            presenter.onSaveButtonPressed(
                mainChangeWallpaperCheckbox.isChecked,
                mainChangeWallpaperTimeInput.text.toString().toLong(),
                DATE.valueOf(mainChangeWallpaperDateSpinner.selectedItem.toString().toUpperCase()),
                mainMainScreenCheckbox.isChecked,
                mainLockScreenCheckbox.isChecked
                )
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "Selected data: $data")

        if (requestCode == PICK_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            data.data?.let { uri ->

                val docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
                val path = ASFUriHelper.getPath(this, docUri)
                presenter.onSelectedFolderChanged(path)
            }

        }
    }

    override fun setSavedSetting(setting: Setting) {
        mainChangeWallpaperCheckbox.isChecked = setting.changeWallpaper
        mainChangeWallpaperTimeInput.setText(setting.timeLength.toString())
        mainChangeWallpaperDateSpinner.setSelection(setting.date.ordinal)
        mainMainScreenCheckbox.isChecked = setting.changeMainScreen
        mainLockScreenCheckbox.isChecked = setting.changeLockScreen
    }

    override fun setFolderInfo(folderName: String, images: Int, thumbnail: Bitmap?) {
        mainSelectedFolderImage.visibility = View.VISIBLE
        mainSelectedFolderText.text = this.getString(R.string.folder_description, folderName, images)
        thumbnail?.let { it ->
            mainSelectedFolderImage.setImageBitmap(it)
        }
    }

    override fun showFileSelector() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
        startActivityForResult(Intent.createChooser(intent, "Choose directory"), PICK_DIRECTORY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun showSettingSavedMessage() {
        makeToast(this.getString(R.string.setting_saved))
    }

    override fun showFolderNotSetError() {
        makeToast(this.getString(R.string.folder_not_set_error))
    }

    override fun scheduleWallpaperChange(setting: Setting) {
        WallpaperChangerScheduler.scheduleWallpaperChange(this, setting)
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
