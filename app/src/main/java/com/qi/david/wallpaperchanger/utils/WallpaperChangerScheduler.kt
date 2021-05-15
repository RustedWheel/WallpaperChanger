package com.qi.david.wallpaperchanger.utils

import com.qi.david.wallpaperchanger.model.Setting
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.qi.david.wallpaperchanger.BroadcastReceivers.WallpaperChangerBroadcastReceiver
import com.qi.david.wallpaperchanger.model.DATE
import java.util.concurrent.TimeUnit


class WallpaperChangerScheduler {

    companion object {
        private const val WALLPAPER_CHANGER_ID = 12345

        fun scheduleWallpaperChange(context: Context, setting: Setting) {

            if(setting.changeWallpaper) {

                val wallpaperChangeEventInterval = when (setting.date) {
                    DATE.WEEKS -> {
                        TimeUnit.DAYS.toMillis(setting.timeLength * 7)
                    }
                    DATE.DAYS -> {
                        TimeUnit.DAYS.toMillis(setting.timeLength)
                    }
                    DATE.HOURS -> {
                        TimeUnit.HOURS.toMillis(setting.timeLength)
                    }
                    DATE.MINUTES -> {
                        TimeUnit.MINUTES.toMillis(setting.timeLength)
                    }
                }

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val myIntent = Intent(context, WallpaperChangerBroadcastReceiver::class.java)
                myIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)

                val pendingIntent = PendingIntent.getBroadcast(context, WALLPAPER_CHANGER_ID, myIntent, 0)

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), wallpaperChangeEventInterval, pendingIntent)

            } else {

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val myIntent = Intent(context, WallpaperChangerBroadcastReceiver::class.java)

                val pendingIntent = PendingIntent.getBroadcast(context, WALLPAPER_CHANGER_ID, myIntent, 0)

                alarmManager.cancel(pendingIntent)
            }
        }
    }
}