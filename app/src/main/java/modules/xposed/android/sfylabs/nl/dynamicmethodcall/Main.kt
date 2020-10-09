package modules.xposed.android.sfylabs.nl.dynamicmethodcall


import android.annotation.SuppressLint
import android.app.Application
import android.content.*
import android.os.Build
import android.support.annotation.RequiresApi
import com.squareup.moshi.Moshi
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Main : IXposedHookLoadPackage {

    companion object {
        lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    }

    private fun logI(msg: String) = XposedBridge.log("[DYNAMIC METHOD CALL INFO] ${lpparam.packageName}: $msg")
    private fun logE(msg: String) = XposedBridge.log("[DYANMIC METHOD CALL ERROR] ${lpparam.packageName}: $msg")

    private var context: Context? = null
        set(value) {
            value?.apply {
                if (context == null) {
                    field = this
                    makeBroadcastReceiver(this)
                    logI("Retrieved the context")
                }
            } ?: logE("Retrieved context was null, this can never happen")
        }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        Main.lpparam = lpparam!!

        findAndHookMethod("android.app.Application", lpparam.classLoader, "onCreate", object: XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                context = (param?.thisObject as Application).applicationContext
            }
        })

        findAndHookMethod("android.app.ApplicationPackageManager", lpparam.classLoader,
                "setComponentEnabledSetting", ComponentName::class.java,
                Int::class.java, Int::class.java, object: XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                param?.args?.set(1, 0) ?: logE("MethodHookParam or args where null, this cannot happen!")

            }
        })
    }

    private fun makeBroadcastReceiver(context: Context) {
        val filter = IntentFilter()
        filter.addAction(Action.CALL.toString())

        val receiver = object: BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.run {
                    logI("Received intent with action: $action")
                    val dataJson = intent.getStringExtra("methodInfoJson")
                    val moshi = Moshi.Builder().build()
                    val dataInfoAdapter = moshi.adapter(Data::class.java)
                    dataInfoAdapter.fromJson(dataJson)
                }?.run {
                    logI("Checking for right package...")

                    if (manifestPackagename != lpparam.packageName!!) {
                        logI("Wrong package, got ${lpparam.packageName}")
                        return
                    }

                    logI("Right package!")

                    val deobfuscated = mutableListOf<String>()

                    for (methodInfo: MethodInfo in data) {
                        val locationPackage = methodInfo.locationPackage
                        val className = methodInfo.className
                        val methodName = methodInfo.methodName
                        val arguments = methodInfo.arguments

                        logI("Calling $locationPackage.$className.$methodName")

                        val clasz = if (locationPackage.isBlank()) {
                            findClass(className, lpparam.classLoader)
                        } else {
                            findClass("$locationPackage.$className", lpparam.classLoader)
                        }

                        val deobfuscatedString = callStaticMethod(clasz, methodName, arguments[0].arg) as String
                        deobfuscated.add(deobfuscatedString)
                    }

                    val result = Result(deobfuscated)
                    val moshir = Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
                    val jsonAdapter = moshir.adapter(Result::class.java)
                    val json = jsonAdapter.toJson(result)

                    logI(json)
                    logI("Going to respond with result...")

                    Intent().apply {
                        action = Action.RESULT.toString()
                        `package` = "modules.xposed.android.sfylabs.nl.dynamicmethodcall"
                        putExtra("uuid", intent.getStringExtra("uuid"))
                        putExtra("result", json)
                        context?.startService(this)
                    }
                } ?: logE("Intent or Data was null")
             }
        }

        context.registerReceiver(receiver, filter)
        logI("registered the broadcastreceiver")
    }
}
