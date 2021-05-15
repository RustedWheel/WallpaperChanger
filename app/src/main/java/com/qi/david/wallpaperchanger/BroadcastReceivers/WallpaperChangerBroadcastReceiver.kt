package com.qi.david.wallpaperchanger.BroadcastReceivers

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import com.qi.david.wallpaperchanger.utils.SettingStorageHelper
import com.qi.david.wallpaperchanger.utils.WallpaperChangerScheduler
import java.io.File
import kotlin.random.Random

class WallpaperChangerBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val storage = SettingStorageHelper(context)
        val setting = storage.getSetting()

        if(setting.changeWallpaper) {

            val folder = File(setting.folderPath)
            val allImages = folder.listFiles { _, name -> name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") }
            val randomInt = Random.nextInt(0, allImages.size - 1)
            val bitmap = BitmapFactory.decodeFile(allImages[randomInt].path)

            if(setting.changeMainScreen) WallpaperManager.getInstance(context).setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
            if(setting.changeLockScreen) WallpaperManager.getInstance(context).setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
        }

    }

}