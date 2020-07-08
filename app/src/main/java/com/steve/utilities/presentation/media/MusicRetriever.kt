package com.steve.utilities.presentation.media

import android.content.ContentResolver
import android.content.ContentUris

class MusicRetriever(private val contentResolver: ContentResolver) {
    fun getRandomItem(): Item? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    data class Item(val id: Long, val artist: String?, val title: String, val album: String?, val duration: Long) {
        val uri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

}