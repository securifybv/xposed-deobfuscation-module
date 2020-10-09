package modules.xposed.android.sfylabs.nl.dynamicmethodcall.server

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import modules.xposed.android.sfylabs.nl.dynamicmethodcall.Action
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.*

class ServerService: Service() {

    private val tag = ServerService::class.java.name

    // Kotlin equivalent of making things static.
    companion object {
        val results: HashMap<String, String?> = HashMap()

        var running: Boolean = false
            private set
        var ip: String? = null
        private const val port = 6000
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(tag, "Starting the server thread...")
        SocketServerThread(applicationContext).start()
        running = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private class SocketServerThread(private val context: Context): Thread() {
        private val tag = SocketServerThread::class.java.name

        override fun run() {
            Log.i(tag, "ServerService running...")

            val serverSocket = ServerSocket(port)

            while (true) {
                try {
                    serverSocket.accept().also { socket ->
                        HandleSocketThread(socket, context).run()
                    }
                } catch (e: SocketException) {
                    Log.e(tag, e.message)
                }
            }
        }
    }

    private class HandleSocketThread(private val socket: Socket, private val context: Context): Thread() {
        private val tag = HandleSocketThread::class.java.name

        override fun run() {
            val inputStream = socket.getInputStream()
            var info: String

            Log.i(tag, "Received connection from: " + socket.inetAddress)


            inputStream.bufferedReader().use {
                val json = it.readLine()

                Log.i(tag, json)
                Log.i(tag, "Sending broadcast...")

                val uuid = UUID.randomUUID().toString()

                Intent().apply {
                    putExtra("methodInfoJson", json)
                    putExtra("uuid", uuid)
                    action = Action.CALL.toString()
                    context.sendBroadcast(this)
                }

                Log.i(tag, "Broadcast send!")

                // Maybe you think it's nasty, but it works.
                while (results[uuid] == null) {
                    sleep(500)
                }

                SocketServerReplyThread(socket, results[uuid]!!).run()
            }
        }
    }

    class SocketServerReplyThread(private val socket: Socket, private val result: String): Thread() {
        private val tag = SocketServerReplyThread::class.java.name

        override fun run() {
            Log.i(tag, "Replying to client")
            Log.i(tag, "Results: $result")

            val outputStream = socket.getOutputStream()

            PrintStream(outputStream).use { output ->
                output.println(result)
                output.close()
            }

            Log.i(tag, "Replied to client")
        }
    }
}