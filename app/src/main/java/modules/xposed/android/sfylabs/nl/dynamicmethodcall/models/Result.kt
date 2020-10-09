package modules.xposed.android.sfylabs.nl.dynamicmethodcall

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Result (
        var deobfuscated: List<String>
)
