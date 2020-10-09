package modules.xposed.android.sfylabs.nl.dynamicmethodcall.server

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StartServerBroadReceiver : BroadcastReceiver() {

    private val tag = StartServerBroadReceiver::class.java.name

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            if (!ServerService.running) {
                Log.i(tag, "Starting the server...")
                val serverIntent = Intent(context, ServerService::class.java)
                context?.startService(serverIntent) ?: Log.e(tag, "The context was null can never happen!")
            }
        } ?: Log.e(tag, "The intent was null, this can never happen!")
    }
}