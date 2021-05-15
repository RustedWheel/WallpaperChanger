package com.qi.david.wallpaperchanger.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.qi.david.wallpaperchanger.model.Setting

class SettingStorageHelper(context: Context) {

    private var storage: SharedPreferences

    init {
        storage = context.getSharedPreferences(SETTING_STORE_NAME, Context.MODE_PRIVATE)
    }

    fun saveSetting(setting: Setting) {
        val editor = storage.edit()
        val json = Gson().toJson(setting)
        editor.putString(DEFAULT_SETTING_ID, json)
        editor.apply()
    }

    fun getSetting(): Setting {
        val settingJson = storage.getString(DEFAULT_SETTING_ID, null)
        settingJson?.let{ setting ->
            return Gson().fromJson<Setting>(setting, Setting::class.java)
        }
        return Setting()
    }

    companion object {
        private const val SETTING_STORE_NAME = "SETTING"
        private const val DEFAULT_SETTING_ID = "setting"
    }
}