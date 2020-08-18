package com.steve.utilities.common.extensions

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import com.steve.utilities.R
import com.steve.utilities.common.base.SubActivity
import com.steve.utilities.core.extensions.Array2D
import com.steve.utilities.domain.model.Cell
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun Context?.startActivity(fragment: Class<*>) {
    SubActivity.start(this, fragment)
}

@Throws(IOException::class)
fun Context?.readGameBoards(): Array2D<Cell?> {
    val inputStream = this?.resources?.openRawResource(R.raw.easy) ?: return Array2D()
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    var line = bufferedReader.readLine()
    val result = Array2D<Cell>(9, 9)
    var yy = 0
    while (line != null) {
        line.split("")
            .filter { it.toIntOrNull() != null }
            .forEachIndexed { xx, s ->
                val cell = Cell().apply {
                    x = xx
                    y = yy
                    value = s.toInt()
                    isEditable = s == "0"
                }
                result[xx, yy] = cell
            }
        yy++
        line = bufferedReader.readLine()
    }
    bufferedReader.close()
    return result
}

fun Service?.createNotificationChannel(id: String,
                                       name: String, description: String? = null,
                                       @SuppressLint("InlinedApi") importance: Int = NotificationManager.IMPORTANCE_DEFAULT) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val channel = NotificationChannel(id, name, importance)
        .apply {
            this.description = description
        }
    val notificationManager = this?.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
    notificationManager?.createNotificationChannel(channel)
}