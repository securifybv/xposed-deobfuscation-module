package modules.xposed.android.sfylabs.nl.dynamicmethodcall

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data (
    val data: List<MethodInfo>,
    val manifestPackagename: String
)

@JsonClass(generateAdapter = true)
data class MethodInfo (
        val locationPackage: String,
        val className: String,
        val static: Boolean,
        val methodName: String,
        val arguments: List<Arguments>
)

@JsonClass(generateAdapter = true)
data class Arguments (
        val arg: String,
        val type: String
)