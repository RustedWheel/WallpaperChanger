package com.qi.david.wallpaperchanger.model

enum class DATE {
    WEEKS,
    DAYS,
    HOURS,
    MINUTES
}

data class Setting(var folderPath: String? = null, var changeWallpaper: Boolean= false, var timeLength: Long = 0, var date: DATE = DATE.MINUTES, var changeMainScreen: Boolean = false, var changeLockScreen: Boolean = false)