package modules.xposed.android.sfylabs.nl.dynamicmethodcall.server

import android.app.IntentService
import android.content.Intent
import android.util.Log

class ResultIntentService: IntentService("ResultIntentService"){
    private val tag = ResultIntentService::class.java.toString()


    override fun onHandleIntent(intent: Intent?) {
        intent?.apply {
            val result = getStringExtra("result")!!
            val uuid = getStringExtra("uuid")!!

            Log.i(tag, "Handing result to server, uuid: $uuid result: $result")

            ServerService.results[uuid] = result

            Log.i(tag, "Handed sockets to server")
        } ?: Log.e(tag, "The intent was null, this cannot happen")
    }

}