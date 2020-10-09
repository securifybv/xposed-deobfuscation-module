package modules.xposed.android.sfylabs.nl.dynamicmethodcall

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import modules.xposed.android.sfylabs.nl.dynamicmethodcall.server.ServerService
import modules.xposed.androiod.sfylabs.nl.dynamicmethodcall.R
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {
    private val tag = MainActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val infoIp = findViewById<TextView>(R.id.ipInfo)
        Log.i(tag, "Showing IP on screen...")
        infoIp.text = getIp() ?: "Unknown"

        if (!ServerService.running) {
            Log.i(tag, "Starting server...")
            Intent(this, ServerService::class.java).also { intent ->
                startService(intent)
            }
        }
    }

    private fun getIp(): String? {
        val enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (enumNetworkInterfaces.hasMoreElements()) {
            val networkInterface = enumNetworkInterfaces.nextElement()
            val enumInetAddress = networkInterface.inetAddresses

            while (enumInetAddress.hasMoreElements()) {
                val inetAddress = enumInetAddress.nextElement()

                if (inetAddress.isSiteLocalAddress) {
                    return inetAddress.hostAddress

                }
            }
        }
        return null
    }
}
