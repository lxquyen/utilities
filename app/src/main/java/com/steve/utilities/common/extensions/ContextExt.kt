package com.steve.utilities.common.extensions

import android.content.Context
import com.steve.utilities.R
import com.steve.utilities.common.base.SubActivity
import com.steve.utilities.core.extensions.Array2D
import com.steve.utilities.domain.model.Cell
import timber.log.Timber
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
    var row = 0
    while (line != null) {
        line.split("")
            .filter { it.toIntOrNull() != null }
            .forEachIndexed { index, s ->
                val cell = Cell().apply {
                    x = row
                    y = index
                    value = s.toInt()
                    isEditable = s == "0"
                }
                result[row, index] = cell
            }
        row++
        line = bufferedReader.readLine()
    }
    bufferedReader.close()
    return result
}