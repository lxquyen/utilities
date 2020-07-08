package com.steve.utilities.presentation.media

import android.os.AsyncTask

class PrepareMusicRetrieverTask(
    private val retriever: MusicRetriever,
    private val listener: MusicRetrieverPreparedListener) : AsyncTask<Unit, Unit, Unit>() {

    override fun doInBackground(vararg params: Unit?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface MusicRetrieverPreparedListener {

    }
}