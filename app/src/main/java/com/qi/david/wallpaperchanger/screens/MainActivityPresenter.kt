package com.qi.david.wallpaperchanger.screens

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.qi.david.wallpaperchanger.model.DATE
import com.qi.david.wallpaperchanger.model.Setting
import com.qi.david.wallpaperchanger.utils.SettingStorageHelper
import java.io.File

interface MainActivityView {
    fun showFileSelector()
    fun setSavedSetting(setting: Setting)
    fun setFolderInfo(folderName: String, images: Int, thumbnail: Bitmap?)
    fun showSettingSavedMessage()
    fun showFolderNotSetError()
    fun scheduleWallpaperChange(setting: Setting)
}

class MainActivityPresenter(private val view: MainActivityView) {

    private var setting: Setting? = null
    private var storage: SettingStorageHelper? = null

    fun bindStorage(settingStorage: SettingStorageHelper) {
        this.storage = settingStorage
        this.setting = settingStorage.getSetting()
        onSettingStateChanged()
    }

    fun onSettingStateChanged() {
        setting?.let { currentSetting ->
            currentSetting.folderPath?.let { folder ->
                onSelectedFolderChanged(folder)
            }
            view?.setSavedSetting(currentSetting)
        }
    }

    fun onSelectedFolderChanged(folderPath: String) {

        setting?.let { setting ->
            setting.folderPath = folderPath
        }

        val folder = File(folderPath)
        val allImages =
            folder.listFiles { _, name -> name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") }
        val bitmap = BitmapFactory.decodeFile(allImages[0].path)
        view?.setFolderInfo(folder.name, allImages.size, bitmap)
    }

    fun onSelectFolderButtonPressed() {
        view?.showFileSelector()
    }

    fun onSaveButtonPressed(
        changeWallpaper: Boolean,
        timeLength: Long,
        date: DATE,
        changeMainScreen: Boolean,
        changeLockScreen: Boolean
    ) {

        setting?.let { setting ->

            setting.changeWallpaper = changeWallpaper
            setting.timeLength = timeLength
            setting.date = date
            setting.changeMainScreen = changeMainScreen
            setting.changeLockScreen = changeLockScreen

            if (setting.folderPath != null) {

                storage?.let { storage ->
                    storage.saveSetting(setting)
                    view?.showSettingSavedMessage()
                    view?.scheduleWallpaperChange(setting)
                }

            } else {
                view?.showFolderNotSetError()
            }
        }
    }
}