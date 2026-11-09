package com.northstar.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import org.osmdroid.config.Configuration

@HiltAndroidApp
class NorthStarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val base = File(cacheDir, "osmdroid")
        val tiles = File(base, "tiles")
        tiles.mkdirs()
        Configuration.getInstance().apply {
            osmdroidBasePath = base
            osmdroidTileCache = tiles
            userAgentValue = packageName
            load(this@NorthStarApp, getSharedPreferences("osmdroid", MODE_PRIVATE))
        }
    }
}
