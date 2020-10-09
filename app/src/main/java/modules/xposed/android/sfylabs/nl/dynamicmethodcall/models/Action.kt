package modules.xposed.android.sfylabs.nl.dynamicmethodcall

enum class Action {
    CALL,
    RESULT,
    UNKNOWN,
}

fun String?.toAction(): Action {
    return when (this) {
        "CALL" -> Action.CALL
        "RESULT" -> Action.RESULT
        else -> Action.UNKNOWN
    }
}